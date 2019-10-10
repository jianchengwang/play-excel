
package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.E;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    private ThreadLocal<DateFormat> df;

    public DateConverter(String pattern) {
        this.df = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
    }

    @Override
    public Date stringToR(String value, Class clazz) {
        try {
            if(null == value){
                return null;
            }
            return df.get().parse(value);
        } catch (Exception e) {
           throw E.converterException("convert [" + value + "] to Date error", e);
        }
    }

    @Override
    public String toString(Date date) {
        try {
            if(null == date){
                return null;
            }
            return df.get().format(date);
        } catch (Exception e) {
            throw E.converterException("convert [" + date + "] to String error", e);
        }
    }

}
