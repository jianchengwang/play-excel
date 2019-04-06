
package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsgConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ReaderWith2003 extends ReaderConverter implements ExcelReader {

    private Workbook workbook;

    public ReaderWith2003(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public <T> void readExcel(Reader reader) throws ReaderException {
        Class             type    = reader.table().modelType();
        Stream.Builder<Table<T>> builder = Stream.builder();

        try {
            this.initFieldConverter(type.getDeclaredFields());

            Table tableConfig = reader.table();
            ExtMsgConfig extMsgConfig = tableConfig.extMsgConfig();
            boolean haveHeadTitle = tableConfig.haveHeadTitle();
            boolean haveExtMsg = extMsgConfig.haveExtMsg();

            int extMsgRow = 0;
            if(haveExtMsg) extMsgRow = extMsgConfig.extMsgRow();
            int extMsgCol = extMsgConfig.extMsgCol();
            int extMsgColSpan = extMsgConfig.extMsgColSpan();

            boolean isGetSingleSheet = StrKit.isNotEmpty(tableConfig.sheetName()) || tableConfig.sheetIndex()>-1;
            for(int si=0; si<workbook.getNumberOfSheets(); si++) {

                Sheet sheet = workbook.getSheetAt(si);
                if(isGetSingleSheet) {
                    sheet = getSheet(reader);
                }

                Table<T> table = Table.create(tableConfig.modelType(), si, sheet.getSheetName()).initExtMsgList(extMsgConfig.extMsgTotal()).haveHeadTitle(haveHeadTitle);

                int startRow = 0;
                if(haveHeadTitle) {
                    startRow += 1;

                    table.headTitle(sheet.getRow(0).getCell(0).getStringCellValue());
                }

                if(haveExtMsg) {

                    List<ExtMsg> extMsgList = table.extMsgList();
                    for(int ri=0; ri<extMsgRow; ri++) {
                        Row row = sheet.getRow(ri + startRow);
                        if (null == row) {
                            continue;
                        }

                        for(int ci=0; ci<extMsgCol; ci++) {

                            Cell cellTitle = row.getCell(ci + (extMsgColSpan+1) * ci);
                            Cell cellMsg = row.getCell(ci + (extMsgColSpan+1) * ci + 1);

                            ExtMsg extMsg = extMsgList.get(ri);
                            extMsg.setTitle(getCellValue(cellTitle));
                            extMsg.setMsg(getCellValue(cellMsg));

                        }
                    }
                }

                if(haveExtMsg) startRow = startRow + extMsgRow + 1 + table.headLineRow();
                int totalRow = sheet.getPhysicalNumberOfRows();

                List<T> data = new ArrayList<>();
                for (int ri = startRow; ri <= totalRow; ri++) {

                    Row row = sheet.getRow(ri);
                    if (null == row) {
                        continue;
                    }

                    Object instance = type.newInstance();
                    for (Field field : fieldIndexes.values()) {
                        this.writeFiledValue(row, instance, field);
                    }

                    data.add((T) instance);
                }

                table.data(data);

                builder.add(table);

                if(isGetSingleSheet) break;
            }

            reader.tableStream(builder.build());

        } catch (Exception e) {
            throw new ReaderException(e);
        }
    }

    public Sheet getSheet(Reader reader) {
        return StrKit.isNotEmpty(reader.table().sheetName()) ?
                workbook.getSheet(reader.table().sheetName()) : workbook.getSheetAt(reader.table().sheetIndex());
    }

    public Object getCellValue(Field field, Cell cell) throws ConverterException {
        Converter<String, ?> converter = fieldConverters.get(field);

        if (null == converter) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() != CellType.NUMERIC) {
            return converter.stringToR(cell.getStringCellValue());
        }
        if (isDateType(field.getType())) {
            Date javaDate = DateUtil.getJavaDate(cell.getNumericCellValue());
            if (field.getType().equals(Date.class)) {
                return javaDate;
            } else if (field.getType().equals(LocalDate.class)) {
                return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (field.getType().equals(LocalDateTime.class)) {
                return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            return null;
        } else {
            return converter.stringToR(cell.getNumericCellValue() + "");
        }
    }

    public String getCellValue(Cell cell) throws ConverterException {

        if(cell == null) return "";

        if(cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }

        return cell.getStringCellValue();
    }

    private boolean isDateType(Class<?> type) {
        return Date.class.equals(type) || LocalDate.class.equals(type) || LocalDateTime.class.equals(type);
    }
}
