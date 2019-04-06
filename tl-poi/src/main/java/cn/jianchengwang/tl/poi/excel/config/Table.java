package cn.jianchengwang.tl.poi.excel.config;

import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsgConfig;
import cn.jianchengwang.tl.poi.excel.config.style.StyleConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Table<T> {

    private int startRow = 0;

    private String headTitle; // 标题
    private boolean haveHeadTitle;

    private int headLineRow = 1; // default 1

    private int sheetIndex = -1; // default -1
    private String sheetName; // sheet 名称

    private Class<T> modelType; // 类型

    private ExtMsgConfig extMsgConfig;
    private List<ExtMsg> extMsgList; // 附加信息

    private long totalRow;
    private Collection<T> data = new ArrayList<>();

    private StyleConfig styleConfig;


    public Table(Class<T> modelType) {
        this.modelType = modelType;
        this.headLineRow = 1;

        this.extMsgConfig = ExtMsgConfig.create();
    }

    public Table(Class<T> modelType, int sheetIndex, String sheetName) {
        this.modelType = modelType;

        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;

        this.headLineRow = 1;

        this.extMsgConfig = ExtMsgConfig.create();

    }

    public static Table create(Class modelType) {
        return new Table(modelType);
    }

    public static Table create(Class modelType, int sheetIndex, String sheetName) {
        return new Table(modelType, sheetIndex, sheetName);
    }

    public Table startRow(int startRow) {
        this.startRow = startRow;
        return this;
    }

    public int startRow() {
        return this.startRow;
    }

    public Table headTitle(String headTitle) {
        this.headTitle = headTitle;

        if(headTitle != null) this.haveHeadTitle = true;
        return this;
    }

    public String headTitle() {
        return this.headTitle;
    }

    public Table<T> haveHeadTitle(boolean haveHeadTitle) {
        this.haveHeadTitle = haveHeadTitle;
        return this;
    }

    public boolean haveHeadTitle() {
        return this.haveHeadTitle;
    }


    public Table headLineRow(int headLineRow) {
        this.headLineRow = headLineRow;
        return this;
    }

    public int headLineRow() {
        return this.headLineRow;
    }

    public Table sheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    public int sheetIndex() {
        return this.sheetIndex;
    }

    public Table sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public String sheetName() {
        return this.sheetName;
    }

    public Class<T> modelType() {
        return modelType;
    }

    public Table extMsgList(List<ExtMsg> extMsgList) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList);

        return this;
    }

    public Table extMsgList(List<ExtMsg> extMsgList, int extMsgCol) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList, extMsgCol);

        return this;
    }

    public Table extMsgList(List<ExtMsg> extMsgList, int extMsgCol, int extMsgColSpan) {

        this.extMsgList = extMsgList;

        this.extMsgConfig = ExtMsgConfig.create(extMsgList, extMsgCol, extMsgColSpan);

        return this;
    }

    public Table initExtMsgList(int extMsgTotal) {
        this.extMsgList = new ArrayList<>();
        for(int i=0; i<extMsgTotal; i++) {
            extMsgList.add(new ExtMsg());
        }

        return this;
    }

    public Table extMsgConfig(int extMsgTotal) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal);

        this.extMsgConfig = extMsgConfig;

        return this;
    }

    public Table extMsgConfig(int extMsgTotal, int extMsgCol) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal, extMsgCol);

        this.extMsgConfig = extMsgConfig;

        return this;
    }

    public Table extMsgConfig(int extMsgTotal, int extMsgCol, int extMsgColSpan) {

        ExtMsgConfig extMsgConfig = ExtMsgConfig.create(extMsgTotal, extMsgCol, extMsgColSpan);

        this.extMsgConfig = extMsgConfig;

        return this;
    }

    public ExtMsgConfig extMsgConfig() { return extMsgConfig; }

    public List<ExtMsg> extMsgList() {
        return extMsgList;
    }

    public Table<T> data(Collection<T> data) {
        this.data = data;
        this.totalRow = data.size();
        return this;
    }

    public Collection<T> data() {
        return data;
    }

    public Long totalRow() {
        return totalRow;
    }

    public void calTotalRow() {
        this.totalRow = data.size();
    }

    public Table<T> styleConfig(StyleConfig styleConfig) {
        this.styleConfig = styleConfig;
        return this;
    }

    public StyleConfig styleConfig() {
        return this.styleConfig;
    }

}
