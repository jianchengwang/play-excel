package cn.jianchengwang.tl.poi.excel.converter;

import cn.jianchengwang.tl.common.EnumTool;
import cn.jianchengwang.tl.common.base.IBaseEnum;

/**
 * 自定义注解转换器
 * Created by wjc on 2019/9/17
 **/
public class EnumConverter implements Converter<String, IBaseEnum>  {

    @Override
    public IBaseEnum stringToR(String value, Class clazz) {
        return EnumTool.getEnumByDescription(clazz, value);
    }

    @Override
    public String toString(IBaseEnum fieldValue) {
        return (String) fieldValue.getDescription();
    }
}
