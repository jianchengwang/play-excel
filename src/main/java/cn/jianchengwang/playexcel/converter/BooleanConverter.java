
package cn.jianchengwang.playexcel.converter;

public class BooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean stringToR(String value) {
        return Boolean.parseBoolean(value);
    }

}
