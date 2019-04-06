package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.poi.excel.exception.ConverterException;

public final class NullConverter implements Converter<String, Object> {


    @Override
    public Object stringToR(String value) throws ConverterException {
        throw new RuntimeException("Not accessible here");
    }
}
