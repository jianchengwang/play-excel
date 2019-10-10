package cn.jianchengwang.tl.common.exception;

/**
 * Could be used when programmer think it is not logic to reach somewhere.
 * Created by wjc on 2019/8/26
 **/
public class UnexpectedException extends RuntimeException {

    public UnexpectedException() {
        super();
    }

    public UnexpectedException(String message){
        super(message);
    }

    public UnexpectedException(String message, Object... args){
        super(String.format(message, args));
    }

    public UnexpectedException(Throwable cause){
        super(cause);
    }

    public UnexpectedException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }
}
