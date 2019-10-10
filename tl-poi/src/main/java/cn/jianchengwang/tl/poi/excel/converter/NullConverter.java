package cn.jianchengwang.tl.poi.excel.converter;

public final class NullConverter implements Converter<String, Object> {


    @Override
    public Object stringToR(String value, Class clazz) {
        throw new RuntimeException("Not accessible here");
    }
}
