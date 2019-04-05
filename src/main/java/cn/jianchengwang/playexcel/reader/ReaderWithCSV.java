
package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.annotation.ExcelColumn;
import cn.jianchengwang.playexcel.converter.Converter;
import cn.jianchengwang.playexcel.exception.ReaderException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.stream.Stream;

@Slf4j
public class ReaderWithCSV extends ReaderConverter implements ExcelReader {

    private InputStream inputStream;

    public ReaderWithCSV(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public <T> void readExcel(Reader reader) throws ReaderException {
        Class type = reader.table().modelType();

        try {
            this.initFieldConverter(type.getDeclaredFields());
        } catch (Exception e) {
            throw new ReaderException(e);
        }

        Stream.Builder<T> builder = Stream.builder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, reader.charset()))) {

            int startRow = reader.table().headLineRow();

            int    pos  = 0;
            String line = "";
            while ((line = br.readLine()) != null) {
                if (pos++ < startRow) {
                    continue;
                }
                Object   instance = type.newInstance();
                String[] csvLine  = line.split(",");
                this.csvLineToInstance(instance, csvLine);
                builder.add((T) instance);
            }
            reader.stream(builder.build());
        } catch (Exception e) {
            throw new ReaderException(e);
        }
    }

    private void csvLineToInstance(Object instance, String[] csvLine) {
        for (Field field : fieldIndexes.values()) {
            ExcelColumn column = field.getAnnotation(ExcelColumn.class);
            try {
                if (csvLine.length < (column.index() + 1)) {
                    continue;
                }
                Object    cellValue = csvLine[column.index()];
                Converter converter = fieldConverters.get(field);
                if (null != converter) {
                    cellValue = converter.stringToR(csvLine[column.index()]);
                }
                field.set(instance, cellValue);
            } catch (Exception e) {
                log.error("write value {} to field {} failed", csvLine[column.index()], field.getName(), e);
            }
        }
    }

}
