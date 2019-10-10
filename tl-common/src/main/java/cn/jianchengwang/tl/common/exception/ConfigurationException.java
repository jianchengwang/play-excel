package cn.jianchengwang.tl.common.exception;

/**
 * Created by wjc on 2019/8/26
 **/
public class ConfigurationException extends UnexpectedException {

    public ConfigurationException(String message){
        super(message);
    }

    public ConfigurationException(String message, Object... args){
        super(message, args);
    }

    public ConfigurationException(Throwable cause){
        super(cause);
    }

    public ConfigurationException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}

