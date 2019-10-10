
package cn.jianchengwang.tl.poi.excel.converter;

public class BooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean stringToR(String value, Class clazz) {
        return Boolean.parseBoolean(value);
    }

}
