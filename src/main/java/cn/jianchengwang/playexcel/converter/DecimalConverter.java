
package cn.jianchengwang.playexcel.converter;


import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.kit.StrKit;

import java.math.BigDecimal;

public class DecimalConverter extends NumberConverter implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return new BigDecimal(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to BigDecimal error", e);
        }
    }

}
