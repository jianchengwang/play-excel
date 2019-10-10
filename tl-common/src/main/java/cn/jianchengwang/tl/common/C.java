package cn.jianchengwang.tl.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wjc on 2019/9/2
 **/
@UtilityClass
public class C {

    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        List<Field> fields = ReflectionTool.getFieldList(clazz);

        try {
            for (Field field : fields) {
                // 如果是枚举类则排除static根final字段
                if(clazz.isEnum()) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                }
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public static Map<String, String> object2MapWitchStringValue(Object obj) {
        Map<String, Object> map = object2Map(obj);
       return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue()));
    }

    public static Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();

            List<Field> fields = ReflectionTool.getFieldList(obj.getClass());
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}

