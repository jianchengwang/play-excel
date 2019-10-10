package cn.jianchengwang.tl.common;

import cn.jianchengwang.tl.common.base.IBaseEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/8/15
 **/
@UtilityClass
public class EnumTool {

    public static <T extends IBaseEnum> T[] values(Class clazz) {
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Class[" + clazz + "]不是枚举类型");
        }
        //得到values
        return (T[]) clazz.getEnumConstants();
    }

    public static <T extends IBaseEnum> T getFirstValue(Class clazz) {
        return (T) values(clazz)[0];
    }
    // 枚举转成map list集合
    public static <T extends IBaseEnum> List<Map<String, Object>> valuesForMapList(Class clazz) {
        T[] enums = values(clazz);
        return Arrays.stream(enums).map(e -> {
            Map<String,Object> map = C.object2Map(e);
            map.put("name", e.toString());
            return map;
        }).collect(Collectors.toList());
    }

    public static <T extends IBaseEnum> T getEnumByValue(Class clazz, Object value) {
        T[] enums = values(clazz);
        return Arrays.stream(enums).filter(e -> e.getValue().equals(value)).findFirst().get();
    }

    public static <T extends IBaseEnum> T getEnumByDescription(Class clazz, Object description) {
        T[] enums = values(clazz);
        return Arrays.stream(enums).filter(e -> e.getDescription().equals(description)).findFirst().get();
    }

    public static <T extends IBaseEnum> List<Object> getDescriptionList(Class clazz) {
        T[] enums = values(clazz);
        return Arrays.stream(enums).map(e -> e.getDescription()).collect(Collectors.toList());
    }

    public static <T extends IBaseEnum> String[] getDescriptions(Class clazz) {
        List<Object> descriptionList = getDescriptionList(clazz);
        return descriptionList.toArray(new String[descriptionList.size()]);
    }
}
