
package cn.jianchengwang.playexcel.converter;


import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.kit.StrKit;


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
