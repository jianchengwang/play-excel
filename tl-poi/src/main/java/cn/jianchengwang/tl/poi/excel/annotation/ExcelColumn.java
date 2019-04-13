package cn.jianchengwang.tl.poi.excel.annotation;

import cn.jianchengwang.tl.poi.excel.config.options.Options;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.converter.NullConverter;
import cn.jianchengwang.tl.poi.excel.exception.ConverterException;
import cn.jianchengwang.tl.poi.excel.validator.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {

    String[] title() default {""}; // 列头
    int index() default -1; // cell index
    int width() default -1; // 宽度

    boolean required() default false; // 是否必填
    String comment() default ""; // 批注信息
    Class<? extends Options> options() default Void.class; // 下拉框数据源, 生成模板和验证数据时生效
    String dateFormat() default ""; // 日期格式

    Class<? extends Converter> converter() default Void.class; // 转化器
    Class<? extends Validator> validator() default Void.class; // 校验器

    class Void implements Options, Converter, Validator {

        @Override
        public String[] get() {
            return new String[0];
        }

        @Override
        public String valid(Object value) {
            return null;
        }

        @Override
        public Object stringToR(Object value) throws ConverterException {
            return null;
        }
    }

}
