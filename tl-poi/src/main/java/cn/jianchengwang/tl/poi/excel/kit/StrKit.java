package cn.jianchengwang.tl.poi.excel.kit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StrKit {

    public static boolean isNotEmpty(String value) {
        return null != value && !value.isEmpty();
    }

    public static boolean isEmpty(String value) {
        return null == value || value.isEmpty();
    }

}
