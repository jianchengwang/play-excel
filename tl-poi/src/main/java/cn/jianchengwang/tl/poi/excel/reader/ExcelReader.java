package cn.jianchengwang.tl.poi.excel.reader;


import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;

public interface ExcelReader {

    <T> void readExcel(Reader reader) throws ReaderException;
}
