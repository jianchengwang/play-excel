package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.poi.excel.Reader;

public interface ExcelReader {

    <T> void readExcel(Reader reader);
}
