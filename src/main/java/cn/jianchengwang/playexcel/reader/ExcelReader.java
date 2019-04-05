package cn.jianchengwang.playexcel.reader;


import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;

public interface ExcelReader {

    <T> void readExcel(Reader reader) throws ReaderException;
}
