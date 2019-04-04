package cn.jianchengwang.playexcel.metadata;

import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsg;
import cn.jianchengwang.playexcel.metadata.extmsg.ExtMsgConfig;

import java.util.ArrayList;
import java.util.List;

public class SheetMd<T> {

    private int headLineRow = 1; // default 1

    private int sheetIndex = -1; // default -1
    private String sheetName; // sheet 名称

    private Class<T> modelType; // 类型

    private ExtMsgConfig extMsgConfig;
    private List<ExtMsg> extMsgList; // 附加信息

    private long totalRow;
    private List<T> data = new ArrayList<>();

    public SheetMd(Class<T> modelType) {
        this.modelType = modelType;
        this.headLineRow = 1;

        this.extMsgConfig = ExtMsgConfig.create();
    }

    public SheetMd(Class<T> modelType, int sheetIndex, String sheetName) {
        this.modelType = modelType;

        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;

        this.headLineRow = 1;

        this.extMsgConfig = ExtMsgConfig.create();

    }

    public static SheetMd create(Class modelType) {
        return new SheetMd(modelType);
    }

    public static SheetMd create(Class modelType, int sheetIndex, String sheetName) {
        return new SheetMd(modelType, sheetIndex, sheetName);
    }

    public SheetMd extMsgList(List<ExtMsg> extMsgList) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList);

        return this;
    }

    public SheetMd extMsgList(List<ExtMsg> extMsgList, int extMsgCol) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList, extMsgCol);

        return this;
    }


    public SheetMd extMsgList(List<ExtMsg> extMsgList, int extMsgCol, int extMsgColSpan) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList, extMsgCol, extMsgColSpan);

        return this;
    }

    public SheetMd initExtMsgList(int extMsgTotal) {
        this.extMsgList = new ArrayList<>();
        for(int i=0; i<extMsgTotal; i++) {
            extMsgList.add(new ExtMsg());
        }

        return this;
    }

    public SheetMd extMsgConfig(int extMsgTotal) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal);

        this.extMsgConfig = extMsgConfig;

        return this;
    }

    public SheetMd extMsgConfig(int extMsgTotal, int extMsgCol) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal, extMsgCol);

        this.extMsgConfig = extMsgConfig;

        return this;
    }

    public SheetMd extMsgConfig(int extMsgTotal, int extMsgCol, int extMsgColSpan) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal, extMsgCol, extMsgColSpan);

        this.extMsgConfig = extMsgConfig;

        return this;
    }


    public SheetMd headLineRow(int headLineRow) {
        this.headLineRow = headLineRow;
        return this;
    }

    public int headLineRow() {
        return this.headLineRow;
    }

    public int sheetIndex() {
        return this.sheetIndex;
    }

    public String sheetName() {
        return this.sheetName;
    }

    public Class<T> modelType() {
        return modelType;
    }

    public ExtMsgConfig extMsgConfig() { return extMsgConfig; }

    public List<ExtMsg> extMsgList() {
        return extMsgList;
    }

    public Long totalRow() {
        return totalRow;
    }

    public List<T> data() {
        return data;
    }

    public void data(List<T> data) {
        this.data = data;
        this.totalRow = data.size();
    }
}
