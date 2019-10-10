package cn.jianchengwang.tl.poi.excel.annotation;

import cn.jianchengwang.tl.poi.excel.config.option.Options;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.converter.NullConverter;
import cn.jianchengwang.tl.poi.excel.validator.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wjc on 2019/9/3
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {
    String header(); // 列标题
    String prop() default ""; // 默认字段名
    int index(); // cell index 从0开始，跟cell表格对应
    int width() default -1; // 宽度
    boolean required() default false; // 必填
    boolean groupBy() default false; // 根据字段聚合，聚合会根据根据聚合字段生成多个sheet，只有一个有效

    String comment() default ""; // 批注信息
    Class<? extends Options> options() default Void.class; // 下拉框数据源, 生成模板和验证数据时生效
    String dateFormat() default ""; // 日期格式

    Class<? extends Converter> converter() default NullConverter.class; // 转化器
    Class<? extends Validator> validator() default Void.class; // 校验器

    class Void implements Options, Validator {

        @Override
        public String[] get() {
            return new String[0];
        }

        @Override
        public String valid(Object value) {
            return null;
        }
    }
}
