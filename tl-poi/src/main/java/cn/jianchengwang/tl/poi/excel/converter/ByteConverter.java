
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;

public class ByteConverter implements Converter<String, Byte> {

    @Override
    public Byte stringToR(String value, Class clazz) {
        try {
            return Byte.parseByte(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to Byte error", e);
        }
    }

}
