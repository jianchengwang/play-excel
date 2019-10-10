package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.base.BaseEO;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.converter.ConverterCache;
import cn.jianchengwang.tl.poi.excel.converter.NullConverter;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.validator.Validator;
import cn.jianchengwang.tl.poi.excel.validator.ValidatorCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReaderConverterAndValidator {

    Map<Integer, Field> fieldIndexes;

    Map<Field, Converter<String, ?>> fieldConverters;
    Map<Field, Validator> fieldValidators;

    void initFieldConverterAndValidator(Field[] fields) throws Exception {
        this.fieldConverters = new HashMap<>();
        this.fieldValidators = new HashMap<>();
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

            Validator validator;
            if(column.validator()!=null && !ExcelColumn.Void.class.equals(column.validator())) {
                validator = column.validator().newInstance();
            } else {
                validator = null;
            }
            if(null != validator) {
                fieldValidators.put(field, validator);
            }
        }
    }

    void writeFiledValue(Row row, Object instance, Field field, boolean recordErrorMsg) {
        ExcelColumn column = field.getAnnotation(ExcelColumn.class);
        Cell cell   = row.getCell(column.index());
        if (null == cell) {
            return;
        }
        try {
            Object cellValue = getCellValue(field, cell);
            field.setAccessible(true);
            field.set(instance, cellValue);

            validCellValue(column, cellValue);
        } catch (Exception e) {
            log.error("write value {} to field {} failed", cell.getStringCellValue(), field.getName(), e);
            if(recordErrorMsg) {
                recordErrorMsg(instance, e.getMessage());
            } else {
                throw E.unexpected(e);
            }
        }
    }

    void validCellValue(ExcelColumn column, Object cellValue) throws Exception {
        // 校验器
        if(column.validator()!=null && column.validator()!= ExcelColumn.Void.class) {
            Validator validator = ValidatorCache.getValidator(column.validator());
            if(validator == null) {
                validator = column.validator().newInstance();
                ValidatorCache.addValidator(validator);
            }

            String validError = validator.valid(cellValue);
            if(validError != null) {
                throw new ReaderException(validError);
            }
        }
    }

    void recordErrorMsg(Object instance, String errorMessage) {
        if(instance instanceof BaseEO) {
            BaseEO eo = (BaseEO) instance;
            eo.setSuccess(false);
            StringBuilder errorSb  = new StringBuilder(eo.getErrorMsg()!=null?eo.getErrorMsg():"");
            errorSb.append(";").append(errorMessage);
            eo.setErrorMsg(errorSb.toString());
        }
    }

    public Object getCellValue(Field field, Cell cell) {
        return cell.getStringCellValue();
    }

}
