package cn.jianchengwang.tl.poi.word.enums;

/**
 * Created by wjc on 2019/9/19
 **/
public enum WordType {
    DOC(".doc"),
    DOCX(".docx");

    private String value;

    WordType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
