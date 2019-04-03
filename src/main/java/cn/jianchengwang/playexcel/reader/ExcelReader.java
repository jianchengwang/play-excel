package cn.jianchengwang.playexcel.reader;


import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;

import java.util.stream.Stream;

public interface ExcelReader {

    <T> Stream<T> readExcel(Reader reader) throws ReaderException;

}
