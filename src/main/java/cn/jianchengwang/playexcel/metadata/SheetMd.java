package cn.jianchengwang.playexcel.metadata;

import lombok.Data;

import java.util.List;

public class SheetMd<T> {

    private int headLineRow = 1; // default 1

    private int sheetIndex = -1; // default -1
    private String sheetName; // sheet 名称

    private Class<T> modelType; // 类型

    private boolean haveExtMsg;
    private int extMsgRow = 1; // default 1
    private int extMsgCol = 1; // default 1
    private int extMsgColSpan = 1; // default 1
    private List<ExtMsg> extMsgList; // 附加信息

    private long totalRow;
    private List<T> data;

    public SheetMd(Class<T> modelType) {
        this.modelType = modelType;
        this.haveExtMsg = false;
        this.headLineRow = 1;
    }

    public SheetMd(Class<T> modelType, int sheetIndex, String sheetName) {
        this.modelType = modelType;

        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;

        this.haveExtMsg = false;
        this.headLineRow = 1;
    }

    public static SheetMd create(Class modelType) {
        return new SheetMd(modelType);
    }

    public static SheetMd create(Class modelType, int sheetIndex, String sheetName) {
        return new SheetMd(modelType, sheetIndex, sheetName);
    }

    public SheetMd extMsgList(List<ExtMsg> extMsgList) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        return this;
    }
    public SheetMd extMsgList(List<ExtMsg> extMsgList, int extMsgRow) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        this.extMsgRow = extMsgRow;
        return this;
    }
    public SheetMd extMsgList(List<ExtMsg> extMsgList, int extMsgRow, int extMsgCol) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        this.extMsgRow = extMsgRow;
        this.extMsgCol = extMsgCol;
        return this;
    }
    public SheetMd extMsgList(List<ExtMsg> extMsgList, int extMsgRow, int extMsgCol, int extMsgColSpan) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        this.extMsgRow = extMsgRow;
        this.extMsgCol = extMsgCol;
        this.extMsgColSpan = extMsgColSpan;
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

    public boolean haveExtMsg() {
        return haveExtMsg;
    }

    public List<ExtMsg> extMsgList() {
        return extMsgList;
    }

    public int extMsgRow() {
        return extMsgRow;
    }

    public int extMsgCol() {
        return extMsgCol;
    }

    public int extMsgColSpan() {
        return extMsgColSpan;
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
