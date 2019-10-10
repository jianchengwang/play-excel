
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private DateTimeFormatter formatter;

    public LocalDateTimeConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDateTime stringToR(String value, Class clazz) {
        try {
            if (null == value) {
                return null;
            }
            return LocalDateTime.parse(value, formatter);
        } catch (Exception e) {
            throw E.converterException("convert [" + value + "] to LocalDateTime error", e);
        }
    }

    @Override
    public String toString(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.format(formatter);
    }

}
