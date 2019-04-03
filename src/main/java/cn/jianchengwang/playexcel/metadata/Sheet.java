package cn.jianchengwang.playexcel.metadata;

import lombok.Data;

import java.util.List;

public class Sheet<T> {

    private int headLineRow; // default 1

    private int sheetIndex; // begin 1
    private String sheetName; // sheet 名称

    private Class<T> modelType; // 类型

    private boolean haveExtMsg;
    private List<ExtMsg> extMsgList; // 附加信息
    private int extMsgCol; // default 1

    private Long totalRow;
    private List<T> data;

    public Sheet(Class<T> modelType) {
        this.modelType = modelType;

        this.haveExtMsg = false;
        this.headLineRow = 1;
    }

    public Sheet(Class<T> modelType, int sheetIndex, String sheetName) {
        this.modelType = modelType;

        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;

        this.haveExtMsg = false;
        this.headLineRow = 1;
    }

    public static Sheet create(Class modelType) {
        return new Sheet(modelType);
    }

    public static Sheet create(Class modelType, int sheetIndex, String sheetName) {
        return new Sheet(modelType, sheetIndex, sheetName);
    }

    public Sheet extMsgList(List<ExtMsg> extMsgList) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        this.extMsgCol = 1;
        return this;
    }
    public Sheet extMsgList(List<ExtMsg> extMsgList, int extMsgCol) {
        this.haveExtMsg = true;
        this.extMsgList = extMsgList;
        this.extMsgCol = extMsgCol;
        return this;
    }

    public Sheet headLineRow(int headLineRow) {
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
        return this.sheetName();
    }

    public Class<T> modelType() {
        return modelType;
    }

    public boolean hveExtMsg() {
        return haveExtMsg;
    }

    public List<ExtMsg> extMsgList() {
        return extMsgList;
    }

    public int extMsgCol() {
        return extMsgCol;
    }

    public Long totalRow() {
        return totalRow;
    }

    public List<T> data() {
        return data;
    }
}
