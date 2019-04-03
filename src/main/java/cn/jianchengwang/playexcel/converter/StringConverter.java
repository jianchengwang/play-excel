package cn.jianchengwang.playexcel.converter;

public class StringConverter implements Converter<String, String> {

    @Override
    public String stringToR(String value) {
        return value;
    }

}
