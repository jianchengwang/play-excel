
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

public class DoubleConverter extends NumberConverter implements Converter<String, Double> {

    @Override
    public Double stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to Double error", e);
        }
    }

}
