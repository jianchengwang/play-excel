
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;

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
