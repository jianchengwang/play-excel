
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

import java.math.BigInteger;

public class BigIntConverter extends NumberConverter implements Converter<String, BigInteger> {

    @Override
    public BigInteger stringToR(String value, Class clazz) {
        try {
            value = replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return new BigInteger(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to BigInt error", e);
        }
    }
}
