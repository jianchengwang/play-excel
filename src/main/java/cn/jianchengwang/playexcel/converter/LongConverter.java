
package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;
import cn.jianchengwang.playexcel.kit.StrKit;

/**
 * Long to string converter
 *
 * @author biezhi
 * @date 2018-12-12
 */
public class LongConverter extends NumberConverter implements Converter<String, Long> {

    @Override
    public Long stringToR(String value) throws ConverterException {
        try {
            value = super.replaceComma(value);
            if (StrKit.isEmpty(value)) {
                return null;
            }
            return Long.parseLong(value);
        } catch (Exception e){
            throw new ConverterException("convert [" + value + "] to Long error", e);
        }
    }

}
