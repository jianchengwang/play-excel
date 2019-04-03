package cn.jianchengwang.playexcel.annotation;

import cn.jianchengwang.playexcel.converter.Converter;
import cn.jianchengwang.playexcel.converter.NullConverter;

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
