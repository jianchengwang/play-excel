package cn.jianchengwang.playexcel.reader;


import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.metadata.SheetMd;

import java.util.stream.Stream;

public interface ExcelReader {

    <T> Stream<T> readExcel(Reader reader) throws ReaderException;

    <T> Stream<SheetMd<T>> readExcel1(Reader reader) throws ReaderException;

}
