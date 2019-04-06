package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.converter.ConverterCache;
import cn.jianchengwang.tl.poi.excel.converter.NullConverter;
import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReaderConverter {

    Map<Integer, Field> fieldIndexes;

    Map<Field, Converter<String, ?>> fieldConverters;

    void initFieldConverter(Field[] fields) throws Exception {
        this.fieldConverters = new HashMap<>();
        this.fieldIndexes = new HashMap<>(fields.length);

        for (Field field : fields) {
            ExcelColumn column = field.getAnnotation(ExcelColumn.class);
            if (null == column) {
                continue;
            }
            field.setAccessible(true);
            fieldIndexes.put(column.index(), field);

            Converter converter;
            if (NullConverter.class.equals(column.converter())) {
                converter = ConverterCache.computeConvert(field);
            } else {
                converter = column.converter().newInstance();
            }
            if (null != converter) {
                fieldConverters.put(field, converter);
            }
        }
    }

    void writeFiledValue(Row row, Object instance, Field field) {
        ExcelColumn column = field.getAnnotation(ExcelColumn.class);
        Cell cell   = row.getCell(column.index());
        if (null == cell) {
            return;
        }
        try {
            Object cellValue = getCellValue(field, cell);
            field.set(instance, cellValue);
        } catch (Exception e) {
            log.error("write value {} to field {} failed", cell.getStringCellValue(), field.getName(), e);
        }
    }

    public Object getCellValue(Field field, Cell cell) throws ConverterException {
        return cell.getStringCellValue();
    }

}
