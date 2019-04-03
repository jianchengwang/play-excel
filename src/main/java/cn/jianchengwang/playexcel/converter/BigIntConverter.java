
package cn.jianchengwang.playexcel.converter;


import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.kit.StrKit;

import java.math.BigInteger;

public class BigIntConverter extends NumberConverter implements Converter<String, BigInteger> {

    @Override
    public BigInteger stringToR(String value) throws ConverterException {
        try {
            value = replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return new BigInteger(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to BigInteger error", e);
        }
    }
}
