package cn.jianchengwang.tl.poi.excel.config.extrainfo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的附加信息，然后后面跟着列表，更复杂的表格建议通过模板实现
 * className:c1  teacherName: t1
 * table
 * Created by wjc on 2019/9/6
 **/
@Data
public class ExtraInfo {
    private boolean haveExtraInfo;
    private int total = 1; // default 1
    private int row = 1; // default 1
    private int col = 1; // default 1
    private int colSpan = 1; // default 1
    private List<Info> infoList; // 附加信息列表

    public ExtraInfo(boolean haveExtraInfo) {
        this.haveExtraInfo = haveExtraInfo;
    }

    public static ExtraInfo create(int total) {
        ExtraInfo extraInfo = new ExtraInfo(true);
        extraInfo.setInfoList(new ArrayList<>());
        extraInfo.setTotal(total);
        extraInfo.setRow(extraInfo.total % extraInfo.col == 0 ? extraInfo.total / extraInfo.col: extraInfo.total/ extraInfo.col + 1);
        return extraInfo;
    }
    public static ExtraInfo create(int total, int col) {
        ExtraInfo extraInfo = create(total);
        extraInfo.setCol(col);
        extraInfo.setRow(extraInfo.row % extraInfo.col == 0? extraInfo.row / extraInfo.col: extraInfo.row / extraInfo.col + 1);
        return extraInfo;
    }
    public static ExtraInfo create(int total, int col, int colSpan) {
        ExtraInfo extraInfo = create(total, col);
        extraInfo.setColSpan(colSpan);
        return extraInfo;
    }

    public static ExtraInfo create(List<Info> infoList) {
        ExtraInfo extraInfo = create(infoList.size());
        extraInfo.setInfoList(infoList);
        return extraInfo;
    }
    public static ExtraInfo create(List<Info> infoList, int col) {
        ExtraInfo extraInfo = create(infoList.size(), col);
        extraInfo.setInfoList(infoList);
        return extraInfo;
    }
    public static ExtraInfo create(List<Info> infoList, int col, int colSpan) {
        ExtraInfo extraInfo = create(infoList.size(), col, colSpan);
        extraInfo.setInfoList(infoList);
        return extraInfo;
    }

    public boolean haveExtraInfo() {
        return this.haveExtraInfo;
    }
    public Integer total() {
       return this.total;
    }
    public Integer row() {
        return this.row;
    }
    public Integer col() {
        return this.col;
    }
    public Integer colSpan() {
        return this.colSpan;
    }
    public List<Info> infoList() {
        return this.infoList;
    }

}
