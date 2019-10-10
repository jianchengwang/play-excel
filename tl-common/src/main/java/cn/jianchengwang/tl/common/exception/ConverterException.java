package cn.jianchengwang.tl.common.exception;

public class ConverterException extends UnexpectedException {

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Object... args){
        super(message, args);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }

}
