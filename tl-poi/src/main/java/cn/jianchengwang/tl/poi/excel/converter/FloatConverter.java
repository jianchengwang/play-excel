
package cn.jianchengwang.tl.poi.excel.converter;


import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

public class FloatConverter extends NumberConverter implements Converter<String, Float> {

    @Override
    public Float stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return Float.parseFloat(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to Float error", e);
        }
    }

}
