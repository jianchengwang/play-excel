package cn.jianchengwang.tl.poi.excel;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.reader.ReaderFactory;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by wjc on 2019/9/5
 **/
@Data
public class Reader<T> {
    private InputStream from; // 文件流
    private GridSheet<T> gridSheet; // 表格配置信息，暂时只支持单表格读取

    private Stream<GridSheet<T>> gridSheetStream; // sheet数据流
    private Stream<T> stream; // 所有数据流

    private boolean recordErrorMsg; // 不需要记录错误的话，则发生错误直接抛出异常中断读取操作

    public Reader(GridSheet<T> gridSheet) {
        this.gridSheet = gridSheet;
    }

    public static <T> Reader<T> create(GridSheet<T> gridSheet) {
        return new Reader<>(gridSheet);
    }

    public static <T> Reader<T> create(Class<T> clazz) {
        GridSheet gridSheet = GridSheet.build().clazz(clazz);
        return new Reader<>(gridSheet);
    }

    public Reader from(InputStream form) {
        this.from = form;
        return this;
    }
    public Reader<T> from(File fromFile) {
        try {
            if (null == fromFile || !fromFile.exists()) {
                throw E.illegalArgumentException("excel file must be exist");
            }
            this.from = new FileInputStream(fromFile);
        } catch (Exception e) {
            throw E.unexpected(e);
        }
        return this;
    }

    public Stream<GridSheet<T>> asGridSheetStream() {
        if (this.gridSheet == null) {
            throw E.illegalArgumentException("gridSheet can be not null");
        }

        if (from == null) {
            throw E.illegalArgumentException("Excel source not is null");
        }

        ReaderFactory.readByStream(this);
        return this.gridSheetStream;
    }
    public Stream<T> asStream() {
        this.stream = this.asGridSheetStream().flatMap(sheet -> sheet.data().stream());
        return this.stream;
    }
    public List<T> asList() throws ReaderException {
        Stream<T> stream = this.asStream();
        return stream.collect(toList());
    }
    public Reader stream(Stream<T> stream) {
        this.stream = stream;
        return this;
    }
    public InputStream from() {
        return this.from;
    }
    public GridSheet gridSheet() {
        return this.gridSheet;
    }

    public Reader recordErrorMsg(boolean recordErrorMsg) {
        this.recordErrorMsg = recordErrorMsg;
        return this;
    }
    public boolean recordErrorMsg() {
        return this.recordErrorMsg;
    }
}
