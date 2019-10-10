package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

public class ShortConverter extends NumberConverter implements Converter<String, Short> {

    @Override
    public Short stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return Short.parseShort(value);
        } catch (Exception e) {
            throw E.converterException("convert [" + value + "] to Integer error", e);
        }
    }

}
