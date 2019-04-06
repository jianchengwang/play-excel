package cn.jianchengwang.tl.poi.excel.enums;

import org.apache.poi.poifs.filesystem.FileMagic;

import java.io.IOException;
import java.io.InputStream;

public enum ExcelType {

    XLSX(".xlsx"),
    XLS(".xls"),
    CSV(".csv");

    private String value;

    ExcelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ExcelType valueOf(InputStream inputStream){
        try {
            if (!inputStream.markSupported()) {
                return null;
            }
            FileMagic fileMagic =  FileMagic.valueOf(inputStream);
            if(FileMagic.OLE2.equals(fileMagic)){
                return XLS;
            }
            if(FileMagic.OOXML.equals(fileMagic)){
                return XLSX;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
