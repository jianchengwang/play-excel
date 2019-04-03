
package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;


public class ByteConverter implements Converter<String, Byte> {

    @Override
    public Byte stringToR(String value) throws ConverterException {
        try {
            return Byte.parseByte(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to Byte error", e);
        }
    }

}
