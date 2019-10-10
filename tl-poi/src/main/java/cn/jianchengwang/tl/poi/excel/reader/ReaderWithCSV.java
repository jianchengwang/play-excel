
package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.poi.excel.Const;
import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.validator.Validator;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.stream.Stream;

@Slf4j
public class ReaderWithCSV extends ReaderConverterAndValidator implements ExcelReader {

    private InputStream inputStream;

    public ReaderWithCSV(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public <T> void readExcel(Reader reader) throws ReaderException {
        Class clazz = reader.gridSheet().clazz();

        try {
            this.initFieldConverterAndValidator(clazz.getDeclaredFields());
        } catch (Exception e) {
            throw new ReaderException(e);
        }

        Stream.Builder<T> builder = Stream.builder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, Const.charset))) {

            int startRow = 0;
            int    pos  = 0;
            String line = "";
            while ((line = br.readLine()) != null) {
                if (pos++ < startRow) {
                    continue;
                }
                Object   instance = clazz.newInstance();
                String[] csvLine  = line.split(",");
                this.csvLineToInstance(instance, csvLine, reader.recordErrorMsg());
                builder.add((T) instance);
            }
            reader.stream(builder.build());
        } catch (Exception e) {
            throw new ReaderException(e);
        }
    }

    private void csvLineToInstance(Object instance, String[] csvLine, boolean recordErrorMsg) {
        for (Field field : fieldIndexes.values()) {
            ExcelColumn column = field.getAnnotation(ExcelColumn.class);
            try {
                if (csvLine.length < (column.index() + 1)) {
                    continue;
                }
                Object    cellValue = csvLine[column.index()];
                Converter converter = fieldConverters.get(field);
                if (null != converter) {
                    cellValue = converter.stringToR(csvLine[column.index()], field.getType());
                }
                field.set(instance, cellValue);

                Validator validator = fieldValidators.get(field);
                if(null != validator) {
                    String validError = validator.valid(cellValue);
                    if(validError != null) {
                        throw new ReaderException(validError);
                    }
                }
            } catch (Exception e) {
                log.error("write value {} to field {} failed", csvLine[column.index()], field.getName(), e);
                if(recordErrorMsg) {
                    recordErrorMsg(instance, e.getMessage());
                } else {
                    throw E.unexpected(e);
                }
            }
        }
    }

}
