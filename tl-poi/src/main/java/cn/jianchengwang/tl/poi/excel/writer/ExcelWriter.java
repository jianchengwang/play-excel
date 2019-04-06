package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.poi.excel.Constant;
import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.playexcel.converter.*;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;
import cn.jianchengwang.tl.poi.excel.config.style.StyleConfig;
import cn.jianchengwang.tl.poi.excel.converter.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.comparingInt;

@Slf4j
public abstract class ExcelWriter {

    private int                 rowNum;
    private Map<Integer, Field> fieldIndexes;
    private List<ExcelColumn> columns;

    Workbook workbook;
    OutputStream outputStream;

    ExcelWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    ExcelWriter() {
    }

    void writeSheet(Writer writer) throws WriterException {

        Collection<Table<?>> tables = writer.tables();
        if(!writer.haveMultipleSheet()) {
            tables = new ArrayList<>();
            tables.add(
                    Table.create(Object.class, 0, writer.sheetName())
                            .startRow(writer.startRow())
                            .headLineRow(writer.startRow())
                            .headTitle(writer.headerTitle())
                            .data(writer.rows())
                            .styleConfig(writer.styleConfig())
            );
        }

        for(Table table : tables) {
            writeSheet0(writer, table);
        }

        // write to OutputStream
        try (OutputStream os = outputStream) {
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WriterException("workbook write to OutputStream error", e);
        }

    }

    /**
     * Write data to Excel Sheet
     * <p>
     * 1. create sheet
     * 2. write title(optional)
     * 3. write column header
     * 4. write row
     * 5. write to OutputStream
     *
     * @param writer writer
     * @throws WriterException
     */
    void writeSheet0(Writer writer, Table table) throws WriterException {

        // create sheet
        Sheet sheet = workbook.createSheet(table.sheetName());

        // setting styles
        CellStyle headerStyle = Constant.defaultHeaderStyle(workbook);
        CellStyle columnStyle = Constant.defaultColumnStyle(workbook);
        CellStyle titleStyle  = Constant.defaultTitleStyle(workbook);

        StyleConfig styleConfig = table.styleConfig();
        if(styleConfig != null) {
            if (null != styleConfig.titleStyle()) {
                titleStyle = styleConfig.titleStyle().accept(workbook, titleStyle);
            }
            if (null != styleConfig.headerStyle()) {
                headerStyle = styleConfig.headerStyle().accept(workbook, headerStyle);
            }
            if (null != styleConfig.cellStyle()) {
                columnStyle = styleConfig.cellStyle().accept(workbook, columnStyle);
            }

        }

        if (writer.isRaw()) {
            writer.sheetConsumer().accept(sheet);
        } else {
            // compute the Filed to be written
            Collection<?> rows   = table.data();
            Field[]       fields = rows.iterator().next().getClass().getDeclaredFields();

            this.fieldIndexes = new HashMap<>(fields.length);
            this.columns = new ArrayList<>();

            for (Field field : fields) {
                ExcelColumn column = field.getAnnotation(ExcelColumn.class);
                if (null != column) {
                    field.setAccessible(true);
                    fieldIndexes.put(column.index(), field);
                    columns.add(column);
                }
            }

            int colRowIndex = 0;
            // write title
            String title = table.headTitle();
            if (table.haveHeadTitle()) {
                Integer maxColIndex = columns.stream()
                        .map(ExcelColumn::index)
                        .max(comparingInt(Integer::intValue))
                        .get();

                this.writeHeader(titleStyle, sheet, title, maxColIndex);

                colRowIndex = 1;
            }

            // write extMsg
            if(table.extMsgConfig().haveExtMsg()) {
                this.writeExtMsgList(sheet, table);

                colRowIndex += (table.extMsgConfig().extMsgRow() + 1);
            }

            this.rowNum = table.startRow();
            if (this.rowNum == 0) {
                this.rowNum = colRowIndex + table.headLineRow();
            }

            try {
                // write column header
                this.writeColumnNames(sheet, colRowIndex, headerStyle);

                // write rows
                for (Object row : rows) {
                    this.writeRow(sheet, row, columnStyle);
                }
            } catch (Exception e) {
                log.error("write row fail", e);
            }
        }
    }

    private void writeHeader(CellStyle cellStyle, Sheet sheet, String title, int maxColIndex) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(50);

        for (int i = 0; i <= maxColIndex; i++) {
            Cell cell = titleRow.createCell(i);
            if (i == 0) {
                cell.setCellValue(title);
            }
            if (null != cellStyle) {
                cell.setCellStyle(cellStyle);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColIndex));
    }

    private void writeExtMsgList(Sheet sheet, Table table) {

        int startRow = 0;
        if(table.haveHeadTitle()) startRow = 1;

        int t = table.extMsgConfig().extMsgTotal();
        int r = table.extMsgConfig().extMsgRow();
        int c = table.extMsgConfig().extMsgCol();
        int s = table.extMsgConfig().extMsgColSpan();
        List<ExtMsg> extMsgList = table.extMsgList();

        int extMsgListIndex = 0;
        for(int ri=0; ri<table.extMsgConfig().extMsgRow(); ri++) {

            Row row = sheet.createRow(ri + startRow);

            for(int ci=0; ci<(2 + s)*c-1; ci++) {

                if(extMsgListIndex == extMsgList.size()) return;

                ExtMsg extMsg = extMsgList.get(extMsgListIndex);

                if(ci % (2 + s) == 0) {
                    Cell cell = row.createCell(ci);
                    cell.setCellValue(extMsg.getTitle());
                } else if(ci % (2 + s) == 1){
                    Cell cell = row.createCell(ci);
                    cell.setCellValue(extMsg.getMsg());

                    ci += s;
                    extMsgListIndex++;
                }
            }

        }

    }

    private void writeColumnNames(Sheet sheet, int rowIndex, CellStyle headerStyle) {
        Row rowHead = sheet.createRow(rowIndex);
        for (ExcelColumn column : columns) {
            Cell cell = rowHead.createCell(column.index());
            if (null != headerStyle) {
                cell.setCellStyle(headerStyle);
            }
            cell.setCellValue(column.title());
            if (column.width() > 0) {
                sheet.setColumnWidth(column.index(), column.width());
            } else {
                sheet.setColumnWidth(column.index(), Constant.DEFAULT_COLUMN_WIDTH);
            }
        }
    }

    private void writeRow(Sheet sheet, Object instance, CellStyle columnStyle) throws Exception {
        Row row = sheet.createRow(rowNum++);
        for (Integer index : fieldIndexes.keySet()) {
            Field field = fieldIndexes.get(index);
            if (null == field) {
                continue;
            }

            Object value = field.get(instance);
            if (value == null) {
                continue;
            }

            Cell cell = row.createCell(index);
            if (null != columnStyle) {
                cell.setCellStyle(columnStyle);
            }

            String fieldValue = computeColumnContent(value, field);
            cell.setCellValue(fieldValue);
        }
    }

    String computeColumnContent(Object value, Field field) throws Exception {
        if (field.getType().equals(String.class)) {
            return value.toString();
        }
        ExcelColumn column = field.getAnnotation(ExcelColumn.class);
        if (!NullConverter.class.equals(column.converter())) {
            Converter convert = column.converter().newInstance();
            ConverterCache.addConvert(convert);
            return convert.toString(value);
        } else {
            if (StrKit.isNotEmpty(column.format())) {
                String content = "";
                if (Date.class.equals(field.getType())) {
                    content = new DateConverter(column.format()).toString((Date) value);
                } else if (LocalDate.class.equals(field.getType())) {
                    content = new LocalDateConverter(column.format()).toString((LocalDate) value);
                }
                if (LocalDateTime.class.equals(field.getType())) {
                    content = new LocalDateTimeConverter(column.format()).toString((LocalDateTime) value);
                }
                return content;
            } else {
                Converter converter = ConverterCache.computeConvert(field);
                if (null != converter) {
                    return converter.toString(value);
                } else {
                    return value.toString();
                }
            }
        }
    }

}
