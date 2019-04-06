package cn.jianchengwang.tl.poi.excel.converter;


import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;

public class ShortConverter extends NumberConverter implements Converter<String, Short> {

    @Override
    public Short stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return Short.parseShort(value);
        } catch (Exception e) {
            throw new ConverterException("convert [" + value + "] to Integer error", e);
        }
    }

}
