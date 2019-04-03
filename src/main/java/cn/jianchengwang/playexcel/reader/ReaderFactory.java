package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.kit.ExcelKit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
@UtilityClass
public class ReaderFactory {

    public static <T> Stream<T> readByFile(Reader reader) {
        if (ExcelKit.isXLSX(reader.fromFile())) {
            return new ReaderWith2007(null).readExcel(reader);
        } else {
            if (ExcelKit.isCSV(reader.fromFile())) {
                try {
                    return new ReaderWithCSV(new FileInputStream(reader.fromFile())).readExcel(reader);
                } catch (FileNotFoundException e) {
                    throw new ReaderException(reader.fromFile().getName() + " not found", e);
                }
            } else if (ExcelKit.isXLS(reader.fromFile())) {
                return new ReaderWith2003(ExcelKit.create(reader.fromFile())).readExcel(reader);
            } else {
                throw new ReaderException(reader.fromFile().getName() + " is the wrong format");
            }
        }
    }

    public static <T> Stream<T> readByStream(Reader reader) throws ReaderException {
        byte[] bytes;
        try {
            bytes = ExcelKit.streamAsBytes(reader.fromStream());
        } catch (IOException e) {
            throw new ReaderException(e);
        }

        if (ExcelKit.isXLSX(new ByteArrayInputStream(bytes))) {
            reader.from(new ByteArrayInputStream(bytes));
            return new ReaderWith2007(null).readExcel(reader);
        } else {
            if (ExcelKit.isXLS(new ByteArrayInputStream(bytes))) {
                return new ReaderWith2003(ExcelKit.create(new ByteArrayInputStream(bytes))).readExcel(reader);
            } else {
                return new ReaderWithCSV(new ByteArrayInputStream(bytes)).readExcel(reader);
            }
        }
    }
}