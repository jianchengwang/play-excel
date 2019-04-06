
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;

public class IntConverter extends NumberConverter implements Converter<String, Integer> {

    @Override
    public Integer stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }

            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new ConverterException("convert [" + value + "] to Integer error", e);
        }
    }

}
