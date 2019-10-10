package cn.jianchengwang.tl.common;

import cn.jianchengwang.tl.common.exception.ConfigurationException;
import cn.jianchengwang.tl.common.exception.ConverterException;
import cn.jianchengwang.tl.common.exception.UnexpectedException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception Tool
 * Created by wjc on 2019/8/26
 **/
@Slf4j
@UtilityClass
public class E {

    public static UnexpectedException unexpected(Throwable cause) {
        cause.printStackTrace();
        throw new UnexpectedException(cause);
    }
    public static UnexpectedException unexpected(String message, Object... args) {
        throw new UnexpectedException(message, args);
    }

    public static ConfigurationException invalidConfiguration(Throwable cause) {
        cause.printStackTrace();
        throw new ConfigurationException(cause);
    }
    public static ConfigurationException invalidConfiguration(String message, Object... args) {
        throw new ConfigurationException(message, args);
    }

    public static ConverterException converterException(Throwable cause) {
        cause.printStackTrace();
        throw new ConverterException(cause);
    }
    public static ConverterException converterException(String message, Object... args) {
        throw new ConverterException(message, args);
    }

    public static IllegalArgumentException illegalArgumentException(Throwable cause) {
        cause.printStackTrace();
        throw new IllegalArgumentException(cause);
    }
    public static IllegalArgumentException illegalArgumentException(String message, Object... args) {
        throw new IllegalArgumentException(S.msgFmt(message, args));
    }

    public static void NPE(Object o1) {
        if (null == o1) {
            throw new NullPointerException();
        }
    }

    public static void NPE(Object o1, Object... objects) {
        NPE(o1);
        for (Object o : objects) {
            if (null == o) {
                throw new NullPointerException();
            }
        }
    }
}
