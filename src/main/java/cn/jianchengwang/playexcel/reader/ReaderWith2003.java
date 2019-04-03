
package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.converter.Converter;
import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.kit.StrKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
public class ReaderWith2003 extends ReaderConverter implements ExcelReader {

    private Workbook workbook;

    public ReaderWith2003(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public <T> Stream<T> readExcel(Reader reader) throws ReaderException {
        Class             type    = reader.sheet().modelType();
        Stream.Builder<T> builder = Stream.builder();
        try {
            this.initFieldConverter(type.getDeclaredFields());
            Sheet sheet = getSheet(reader);

            int startRow = reader.sheet().headLineRow();
            int totalRow = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i < totalRow; i++) {
                if (i < startRow) {
                    continue;
                }
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }

                Object instance = type.newInstance();
                for (Field field : fieldIndexes.values()) {
                    this.writeFiledValue(row, instance, field);
                }
                builder.add((T) instance);
            }
            return builder.build();
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

    private boolean isDateType(Class<?> type) {
        return Date.class.equals(type) || LocalDate.class.equals(type) || LocalDateTime.class.equals(type);
    }

}
