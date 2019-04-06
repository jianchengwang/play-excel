package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.poi.excel.exception.ConverterException;

public interface Converter<String, R> {

    R stringToR(String value) throws ConverterException;

    default java.lang.String toString(R fieldValue) throws ConverterException {
        if (null == fieldValue) {
            return null;
        }
        return fieldValue.toString();
    }
}
