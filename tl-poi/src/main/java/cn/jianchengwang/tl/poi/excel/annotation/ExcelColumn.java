package cn.jianchengwang.tl.poi.excel.annotation;

import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.converter.NullConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {

    String title() default "";

    int index() default -1;

    String format() default "";

    Class<? extends Converter> converter() default NullConverter.class;

    int width() default -1;

}
