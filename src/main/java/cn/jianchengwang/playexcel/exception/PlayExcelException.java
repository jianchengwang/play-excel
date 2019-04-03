package cn.jianchengwang.playexcel.exception;

public class PlayExcelException extends Exception {


    public PlayExcelException() {
    }

    public PlayExcelException(String message) {
        super(message);
    }

    public PlayExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayExcelException(Throwable cause) {
        super(cause);
    }
}
