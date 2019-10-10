package cn.jianchengwang.tl.poi.excel.converter;

public interface Converter<String, R> {

    R stringToR(String value, Class clazz);

    default java.lang.String toString(R fieldValue) {
        if (null == fieldValue) {
            return null;
        }
        return fieldValue.toString();
    }
}
