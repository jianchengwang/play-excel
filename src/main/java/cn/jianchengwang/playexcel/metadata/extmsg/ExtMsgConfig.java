package cn.jianchengwang.playexcel.metadata.extmsg;

import lombok.Data;

import java.util.List;

@Data
public class ExtMsgConfig {

    private boolean haveExtMsg;
    private int extMsgTotal = 1; // default 1
    private int extMsgRow = 1; // default 1
    private int extMsgCol = 1; // default 1
    private int extMsgColSpan = 1; // default 1

    public ExtMsgConfig() {
    }

    public ExtMsgConfig(boolean haveExtMsg) {
        this.haveExtMsg = haveExtMsg;
    }

    public static ExtMsgConfig create() {
        return new ExtMsgConfig();
    }

    public static ExtMsgConfig create(int extMsgTotal) {

        ExtMsgConfig extMsgConfig = new ExtMsgConfig(true);
        extMsgConfig.extMsgTotal(extMsgTotal);

        return extMsgConfig;
    }

    public static ExtMsgConfig create(int extMsgTotal, int extMsgCol) {

        ExtMsgConfig extMsgConfig = create(extMsgTotal);
        extMsgConfig.extMsgCol(extMsgCol);
        extMsgConfig.extMsgRow(extMsgTotal % extMsgCol == 0? extMsgTotal / extMsgCol: extMsgTotal / extMsgCol + 1);

        return extMsgConfig;
    }

    public static ExtMsgConfig create(int extMsgTotal, int extMsgCol, int extMsgColSpan) {

        ExtMsgConfig extMsgConfig = create(extMsgTotal, extMsgCol);
        extMsgConfig.extMsgColSpan(extMsgColSpan);

        return extMsgConfig;
    }

    public static ExtMsgConfig create(List<ExtMsg> extMsgList) {
        return create(extMsgList.size());
    }

    public static ExtMsgConfig create(List<ExtMsg> extMsgList, int extMsgCol) {
        return create(extMsgList.size(), extMsgCol);
    }

    public static ExtMsgConfig create(List<ExtMsg> extMsgList, int extMsgCol, int extMsgColSpan) {
        return create(extMsgList.size(), extMsgCol, extMsgColSpan);
    }


    private void extMsgColSpan(int extMsgColSpan) {
        this.extMsgColSpan = extMsgColSpan;
    }

    private void extMsgRow(int extMsgRow) {
        this.extMsgRow = extMsgRow;
    }

    private void extMsgCol(int extMsgCol) {
        this.extMsgCol = extMsgCol;
    }

    private void extMsgTotal(int extMsgTotal) {
        this.extMsgTotal = extMsgTotal;
    }

    public boolean haveExtMsg() {
        return this.haveExtMsg;
    }

    public int extMsgTotal() {
        return this.extMsgTotal;
    }

    public int extMsgCol() {
        return this.extMsgCol;
    }

    public int extMsgRow() {
        return this.extMsgRow;
    }

    public int extMsgColSpan() {
        return this.extMsgColSpan;
    }
}
