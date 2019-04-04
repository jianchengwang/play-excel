package cn.jianchengwang.playexcel.metadata.extmsg;

import lombok.Data;

/**
 * 扩展信息
 */
@Data
public class ExtMsg {

    private String title;
    private String msg;

    public ExtMsg() {
    }

    public ExtMsg(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }
}
