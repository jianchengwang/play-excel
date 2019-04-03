package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;

public interface Converter<String, R> {

    R stringToR(String value) throws ConverterException;

    default java.lang.String toString(R fieldValue) throws ConverterException {
        if (null == fieldValue) {
            return null;
        }
        return fieldValue.toString();
    }
}
