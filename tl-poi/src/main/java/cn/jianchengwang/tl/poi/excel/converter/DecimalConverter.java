
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

import java.math.BigDecimal;

public class DecimalConverter extends NumberConverter implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return new BigDecimal(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to BigDecimal error", e);
        }
    }

}
