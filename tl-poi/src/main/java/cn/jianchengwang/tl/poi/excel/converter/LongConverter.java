
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;

/**
 * Long to string converter
 *
 * @author biezhi
 * @date 2018-12-12
 */
public class LongConverter extends NumberConverter implements Converter<String, Long> {

    @Override
    public Long stringToR(String value, Class clazz) {
        try {
            value = super.replaceComma(value);
            if (S.isEmpty(value)) {
                return null;
            }
            return Long.parseLong(value);
        } catch (Exception e){
            throw E.converterException("convert [" + value + "] to Long error", e);
        }
    }

}
