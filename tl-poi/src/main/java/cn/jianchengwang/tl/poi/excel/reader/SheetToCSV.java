package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.kit.ExcelKit;
import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsgConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.lang.reflect.Field;
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

    private final Table<T> tableConfig;
    private final ExtMsgConfig extMsgConfig;
    private volatile int extMsgListIndex = 0;

    private T row;
    private final Stream.Builder<Table<T>> tableBuilder;
    private Table<T> table;

    public SheetToCSV(OPCPackage opcPackage, Reader reader) {
        this.opcPackage = opcPackage;
        this.type = reader.table().modelType();

        tableConfig = reader.table();
        extMsgConfig = reader.table().extMsgConfig();

        int headTitleRow = tableConfig.haveHeadTitle() ? 1: 0;
        if(extMsgConfig.haveExtMsg()) {
            this.startRow = headTitleRow + extMsgConfig.extMsgRow() + 1 + tableConfig.headLineRow();
        } else this.startRow = headTitleRow + tableConfig.headLineRow();

        tableBuilder = Stream.builder();

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
        table.data().add(row);
    }

    @Override
    public void cell(String cellReference, String formattedValue,
                     XSSFComment comment) {

        if (currentRow < startRow && !tableConfig.haveHeadTitle() && !extMsgConfig.haveExtMsg()) {
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

        if(currentRow == 0 && currentCol == 0 && tableConfig.haveHeadTitle()) {
            table.headTitle(formattedValue);
        }

        if((currentRow+1) < startRow && extMsgConfig.haveExtMsg()) {

            if(tableConfig.haveHeadTitle() && currentRow == 0) return;

            if(extMsgListIndex == table.extMsgList().size()) return;

            ExtMsg extMsg = table.extMsgList().get(extMsgListIndex);

            if(currentCol % (2 + extMsgConfig.extMsgColSpan()) == 0) {
                extMsg.setTitle(formattedValue);
            } else if(currentCol % (2 + extMsgConfig.extMsgColSpan()) == 1){
                extMsg.setMsg(formattedValue);

                extMsgListIndex++;
            }
        } else {

            extMsgListIndex = 0;
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
        table.calTotalRow();
        tableBuilder.add(table);
    }

    public OPCPackage getOpcPackage() {
        return opcPackage;
    }

    public Stream<Table<T>> getTableStream() {
        return tableBuilder.build();
    }

    public void sheetMd(Table<T> table) {
        this.table = table;
    }

}