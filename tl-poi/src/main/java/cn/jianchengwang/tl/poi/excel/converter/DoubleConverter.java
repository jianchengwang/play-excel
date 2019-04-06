
package cn.jianchengwang.tl.poi.excel.converter;


import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;

public class DoubleConverter extends NumberConverter implements Converter<String, Double> {

    @Override
    public Double stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to Double error", e);
        }
    }

}
