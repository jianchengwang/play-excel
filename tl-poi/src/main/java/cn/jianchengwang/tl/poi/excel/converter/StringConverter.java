package cn.jianchengwang.tl.poi.excel.converter;

public class StringConverter implements Converter<String, String> {

    @Override
    public String stringToR(String value, Class clazz) {
        return value;
    }

}
