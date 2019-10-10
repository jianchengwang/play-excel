package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.ExtraInfo;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.Info;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.kit.ExcelKit;
import cn.jianchengwang.tl.poi.excel.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Uses the XSSF Event SAX helpers to do most of the work
 * of parsing the Sheet XML, and outputs the contents
 * as a (basic) CSV.
 */
@Slf4j
public class SheetToCSV<T> extends ReaderConverterAndValidator implements XSSFSheetXMLHandler.SheetContentsHandler {

    private boolean firstCellOfRow;
    private int     currentRow = -1;
    private int     currentCol = -1;

    private final OPCPackage opcPackage;
    private final Class<T>          clazz;
    private final int               startRow;

    private final GridSheet<T> gridSheetConfig;
    private final ExtraInfo extraInfo;
    private final boolean haveExtraInfo;
    private volatile int extraInfoListIndex = 0;

    private T row;
    private final Stream.Builder<GridSheet<T>> gridSheetBuilder;
    private GridSheet<T> gridSheet;

    private final boolean recordErrorMsg;

    public SheetToCSV(OPCPackage opcPackage, Reader reader) {
        this.opcPackage = opcPackage;
        this.clazz = reader.gridSheet().clazz();

        gridSheetConfig = reader.gridSheet();
        extraInfo = reader.gridSheet().extraInfo();
        if(extraInfo!=null ) {
            haveExtraInfo = extraInfo.haveExtraInfo();
        } else {
            haveExtraInfo = false;
        }

        if(haveExtraInfo) {
            this.startRow = extraInfo.row() + 1 + gridSheetConfig.headLineRow();
        } else this.startRow = gridSheetConfig.headLineRow();

        gridSheetBuilder = Stream.builder();

        recordErrorMsg = reader.recordErrorMsg();

        try {
            this.initFieldConverterAndValidator(clazz.getDeclaredFields());
        } catch (Exception e) {
            log.error("init field converter fail", e);
        }
    }

    @Override
    public void startRow(int rowNum) {
        // Prepare for this row
        firstCellOfRow = true;
        currentRow = rowNum;
        currentCol = -1;
        if (currentRow < startRow) {
            return;
        }
        row = ExcelKit.newInstance(clazz);
    }

    @Override
    public void endRow(int rowNum) {
        if (currentRow < startRow) {
            return;
        }
        if(gridSheet.data() == null) {
            gridSheet.data(new ArrayList<>());
        }
        gridSheet.data().add(row);
    }

    @Override
    public void cell(String cellReference, String formattedValue,
                     XSSFComment comment) {

        if (currentRow < startRow && !haveExtraInfo) {
            return;
        }

        if (firstCellOfRow) {
            firstCellOfRow = false;
        }

        // gracefully handle missing CellRef here in a similar way as XSSFCell does
        if (cellReference == null) {
            cellReference = new CellAddress(currentRow, currentCol).formatAsString();
        }

        currentCol = (int) (new CellReference(cellReference)).getCol();

        if((currentRow+1) < startRow && haveExtraInfo) {

            if(extraInfoListIndex == gridSheet.extraInfo().getInfoList().size()) return;

            Info info = gridSheet.extraInfo().infoList().get(extraInfoListIndex);
            if(currentCol % (2 + extraInfo.colSpan()) == 0) {
                info.setK(formattedValue);
            } else if(currentCol % (2 + extraInfo.colSpan()) == 1){
                info.setV(formattedValue);
                extraInfoListIndex++;
            }
        } else {

            extraInfoListIndex = 0;
            Field field = fieldIndexes.get(currentCol);
            if (null != field) {
                try {
                    Object    cellValue = formattedValue;
                    Converter converter = fieldConverters.get(field);
                    if (null != converter) {
                        cellValue = converter.stringToR(formattedValue, field.getType());
                    }
                    field.set(row, cellValue);

                    Validator validator = fieldValidators.get(field);
                    if(null != validator) {
                        String validError = validator.valid(cellValue);
                        if(validError != null) {
                            throw new ReaderException(validError);
                        }
                    }
                } catch (Exception e) {
                    log.error("write field {} value fail", field.getName(), e);
                    if(recordErrorMsg) {
                        recordErrorMsg(row, e.getMessage());
                    } else {
                        throw E.unexpected(e);
                    }
                }
            }
        }

    }

    @Override
    public void endSheet() {
        gridSheet.calTotalRow();
        gridSheetBuilder.add(gridSheet);
    }

    public OPCPackage getOpcPackage() {
        return opcPackage;
    }

    public Stream<GridSheet<T>> getGridSheetStream() {
        return gridSheetBuilder.build();
    }

    public void gridSheet(GridSheet<T> gridSheet) {
        this.gridSheet = gridSheet;
    }

}