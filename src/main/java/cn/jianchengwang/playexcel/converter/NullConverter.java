package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;

public final class NullConverter implements Converter<String, Object> {


    @Override
    public Object stringToR(String value) throws ConverterException {
        throw new RuntimeException("Not accessible here");
    }
}
