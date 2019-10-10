package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.kit.ExcelKit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@UtilityClass
public class ReaderFactory {

    public static <T> void readByStream(Reader reader) {
        byte[] bytes;
        try {
            bytes = ExcelKit.streamAsBytes(reader.from());
        } catch (IOException e) {
            throw new ReaderException(e);
        }

        if (ExcelKit.isXLSX(new ByteArrayInputStream(bytes))) {
            reader.from(new ByteArrayInputStream(bytes));
            new ReaderWith2007(null).readExcel(reader);
        } else {
            if (ExcelKit.isXLS(new ByteArrayInputStream(bytes))) {
                new ReaderWith2003(ExcelKit.create(new ByteArrayInputStream(bytes))).readExcel(reader);
            } else {
                new ReaderWithCSV(new ByteArrayInputStream(bytes)).readExcel(reader);
            }
        }
    }
}