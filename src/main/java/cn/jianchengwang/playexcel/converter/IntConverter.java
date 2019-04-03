
package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.kit.StrKit;

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
