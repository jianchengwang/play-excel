
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

public class IntConverter extends NumberConverter implements Converter<String, Integer> {

    @Override
    public Integer stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }

            return Integer.parseInt(value);
        } catch (Exception e) {
            throw E.converterException("convert [" + value + "] to Integer error", e);
        }
    }

}
