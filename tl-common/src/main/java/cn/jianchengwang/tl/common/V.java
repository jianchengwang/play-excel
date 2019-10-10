package cn.jianchengwang.tl.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Validate Tool
 * Created by wjc on 2019/8/26
 **/
@UtilityClass
public class V {

    private static final Pattern PATTERN_NUMERIC = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
    private static final Pattern PATTERN_EMAIL = Pattern
            .compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PATTERN_MOBILE_PHONE = Pattern.compile("^(1)\\d{10}$");
    private static final Pattern PATTERN_URL = Pattern.compile("(((https|http)?://)?([a-z0-9]+[.])|(www.))"
            + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)".trim());

    public static boolean match(String value, String regularExp) {
        return V.isEmpty(value) ? false
                : Pattern.compile(regularExp).matcher(value).matches();
    }

    /**
     * 判断是否为浮点数或者整数
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isNumeric(String value) {
        return V.isEmpty(value) ? false
                : V.PATTERN_NUMERIC.matcher(value).matches();
    }

    /**
     * 判断是否为正确的邮件格式
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isEmail(String value) {
        return V.isEmpty(value) ? false
                : V.PATTERN_EMAIL.matcher(value).matches();
    }

    /**
     * 判断字符串是否为合法手机号
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isMobile(String value) {
        return V.isEmpty(value) ? false
                : V.PATTERN_MOBILE_PHONE.matcher(value).matches();
    }

    /**
     * 判断字符串是否为合法网址
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isHttpUrl(String value) {
        return V.isEmpty(value) ? false
                : V.PATTERN_URL.matcher(value).matches();
    }

    /**
     * 判断是否为数字
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为空
     *
     * @param value 字符串
     * @return true Or false
     */
    public static boolean isEmpty(String value) {
        return null == value || "".equals(value.trim()) || "null".equalsIgnoreCase(value.trim());
    }

}
