
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ConverterCache {

    private static final Map<Class<? extends Converter>, Converter> CONVERTER_MAP = new HashMap<>(64);

    static {
        CONVERTER_MAP.put(StringConverter.class, new StringConverter());
        CONVERTER_MAP.put(IntConverter.class, new IntConverter());
        CONVERTER_MAP.put(LongConverter.class, new LongConverter());
        CONVERTER_MAP.put(ShortConverter.class, new ShortConverter());
        CONVERTER_MAP.put(ByteConverter.class, new ByteConverter());
        CONVERTER_MAP.put(BooleanConverter.class, new BooleanConverter());
        CONVERTER_MAP.put(DoubleConverter.class, new DoubleConverter());
        CONVERTER_MAP.put(FloatConverter.class, new FloatConverter());
        CONVERTER_MAP.put(DecimalConverter.class, new DecimalConverter());
        CONVERTER_MAP.put(BigIntConverter.class, new BigIntConverter());
    }

    public static void addConvert(Converter converter) {
        if (null != converter) {
            CONVERTER_MAP.put(converter.getClass(), converter);
        }
    }

    public static Converter getConvert(
            Class<? extends Converter> type) {
        return CONVERTER_MAP.get(type);
    }

    public static Converter computeConvert(Field field) throws Exception {
        if (null == field) {
            return null;
        }

        Class fieldType = field.getType();

        ExcelColumn column = field.getAnnotation(ExcelColumn.class);
        if (null != column && !NullConverter.class.equals(column.converter())) {
            return column.converter().newInstance();
        }

        if (fieldType.equals(String.class)) {
            return ConverterCache.getConvert(StringConverter.class);
        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return ConverterCache.getConvert(IntConverter.class);
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            return ConverterCache.getConvert(LongConverter.class);
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return ConverterCache.getConvert(DoubleConverter.class);
        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
            return ConverterCache.getConvert(FloatConverter.class);
        } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            return ConverterCache.getConvert(ShortConverter.class);
        } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
            return ConverterCache.getConvert(ByteConverter.class);
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return ConverterCache.getConvert(BooleanConverter.class);
        } else if (fieldType.equals(BigInteger.class)) {
            return ConverterCache.getConvert(BigIntConverter.class);
        } else if (fieldType.equals(BigDecimal.class)) {
            return ConverterCache.getConvert(DecimalConverter.class);
        }
        else if (fieldType.equals(Date.class)) {
            String pattern = field.getAnnotation(ExcelColumn.class).dateFormat();
            return new DateConverter(pattern);
        } else if (fieldType.equals(LocalDate.class)) {
            String pattern = field.getAnnotation(ExcelColumn.class).dateFormat();
            return new LocalDateConverter(pattern);
        } else if (fieldType.equals(LocalDateTime.class)) {
            String pattern = field.getAnnotation(ExcelColumn.class).dateFormat();
            return new LocalDateTimeConverter(pattern);
        }
        return null;
    }

}
