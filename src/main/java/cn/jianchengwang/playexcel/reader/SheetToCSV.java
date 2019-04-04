package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.converter.Converter;
import cn.jianchengwang.playexcel.kit.ExcelKit;
import cn.jianchengwang.playexcel.metadata.SheetMd;
import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsg;
import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsgConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Uses the XSSF Event SAX helpers to do most of the work
 * of parsing the Sheet XML, and outputs the contents
 * as a (basic) CSV.
 */
@Slf4j
public class SheetToCSV<T> extends ReaderConverter implements XSSFSheetXMLHandler.SheetContentsHandler {

    private boolean firstCellOfRow;
    private int     currentRow = -1;
    private int     currentCol = -1;

    private final OPCPackage opcPackage;
    private final Class<T>          type;
    private final int               startRow;

    private T row;

    private final Stream.Builder<SheetMd<T>> sheetMdBuilder;
    private SheetMd<T> sheetMd;
    private final ExtMsgConfig extMsgConfig;
    private List<ExtMsg> extMsgList;

    public SheetToCSV(OPCPackage opcPackage, Reader reader) {
        this.opcPackage = opcPackage;
        this.type = reader.sheet().modelType();

        sheetMdBuilder = Stream.builder();

        extMsgConfig = reader.sheet().extMsgConfig();
        if(extMsgConfig.haveExtMsg()) {
            this.startRow = extMsgConfig.extMsgRow() + 1 + reader.sheet().headLineRow();
        } else this.startRow = reader.sheet().headLineRow();

        this.sheetMd = SheetMd.create(reader.sheet().modelType()).initExtMsgList(extMsgConfig.extMsgTotal());

        this.extMsgList = reader.sheet().extMsgList();

        try {
            this.initFieldConverter(type.getDeclaredFields());
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
        row = ExcelKit.newInstance(type);
    }

    @Override
    public void endRow(int rowNum) {
        if (currentRow < startRow) {
            return;
        }
        sheetMd.data().add(row);
    }

    @Override
    public void cell(String cellReference, String formattedValue,
                     XSSFComment comment) {

        if (currentRow < startRow && !extMsgConfig.haveExtMsg()) {
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

        if(currentRow < startRow && extMsgConfig.haveExtMsg()) {

            ExtMsg extMsg = extMsgList.get(currentRow);
            if(currentCol % (2 + extMsgConfig.extMsgColSpan()) == 0) {
                extMsg.setTitle(formattedValue);
            } else {
                extMsg.setMsg(formattedValue);
            }
        } else {
            Field field = fieldIndexes.get(currentCol);
            if (null != field) {
                try {
                    Object    cellValue = formattedValue;
                    Converter converter = fieldConverters.get(field);
                    if (null != converter) {
                        cellValue = converter.stringToR(formattedValue);
                    }
                    field.set(row, cellValue);
                } catch (Exception e) {
                    log.error("write field {} value fail", field.getName(), e);
                }
            }
        }

    }

    @Override
    public void endSheet() {
        sheetMdBuilder.add(sheetMd);
    }

    public OPCPackage getOpcPackage() {
        return opcPackage;
    }

    public Stream<SheetMd<T>> getSheetMdStream() {
        return sheetMdBuilder.build();
    }

}