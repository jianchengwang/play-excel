
package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.converter.Converter;
import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.kit.StrKit;
import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsg;
import cn.jianchengwang.playexcel.metadata.SheetMd;
import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsgConfig;
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
        Class             type    = reader.sheet().modelType();
        Stream.Builder<SheetMd<T>> builder = Stream.builder();

        try {
            this.initFieldConverter(type.getDeclaredFields());

            boolean isGetSingleSheet = StrKit.isNotEmpty(reader.sheet().sheetName()) || reader.sheet().sheetIndex()>-1;
            for(int si=0; si<workbook.getNumberOfSheets(); si++) {

                Sheet sheet = workbook.getSheetAt(si);
                if(isGetSingleSheet) {
                    sheet = getSheet(reader);
                }

                int extMsgRow = 0;
                ExtMsgConfig extMsgConfig = reader.sheet().extMsgConfig();
                boolean haveExtMsg = extMsgConfig.haveExtMsg();

                SheetMd<T> sheetMd = SheetMd.create(reader.sheet().modelType(), si, sheet.getSheetName()).initExtMsgList(extMsgConfig.extMsgTotal());

                if(haveExtMsg) {

                    extMsgRow = extMsgConfig.extMsgRow();
                    int extMsgCol = extMsgConfig.extMsgCol();
                    int extMsgColSpan = extMsgConfig.extMsgColSpan();

                    List<ExtMsg> extMsgList = sheetMd.extMsgList();
                    for(int ri=0; ri<extMsgRow; ri++) {
                        Row row = sheet.getRow(ri);
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

                int startRow = reader.sheet().headLineRow();
                int totalRow = sheet.getPhysicalNumberOfRows();
                if(haveExtMsg) startRow = startRow + extMsgRow + 1;

                List<T> data = new ArrayList<>();
                for (int ri = 0; ri < totalRow; ri++) {
                    if (ri < startRow) {
                        continue;
                    }
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

                sheetMd.data(data);

                builder.add(sheetMd);

                if(isGetSingleSheet) break;
            }

            reader.sheetMdStream(builder.build());

        } catch (Exception e) {
            throw new ReaderException(e);
        }
    }

    public Sheet getSheet(Reader reader) {
        return StrKit.isNotEmpty(reader.sheet().sheetName()) ?
                workbook.getSheet(reader.sheet().sheetName()) : workbook.getSheetAt(reader.sheet().sheetIndex());
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
