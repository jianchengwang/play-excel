
package cn.jianchengwang.tl.poi.excel.converter;


import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;


public class FloatConverter extends NumberConverter implements Converter<String, Float> {

    @Override
    public Float stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return Float.parseFloat(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to Float error", e);
        }
    }

}
