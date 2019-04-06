package cn.jianchengwang.tl.poi.excel;

import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.reader.ReaderFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Reader<T> {

    private File fromFile;

    private InputStream fromStream;

    private Charset charset = StandardCharsets.UTF_8;

    private Table<T> table;

    private Stream<T> stream;
    private Stream<Table<T>> tableStream;

    public Reader(Table<T> table) {
        this.table = table;
    }

    public static <T> Reader<T> create(Class<T> modelType) {
        Table sheet = Table.create(modelType);
        return new Reader<>(sheet);
    }

    public static <T> Reader<T> create(Class<T> modelType, File fromFile) {
        Table sheet = Table.create(modelType);
        return new Reader<>(sheet).from(fromFile);
    }

    public static <T> Reader<T> create(Class<T> modelType, InputStream fromStream) {
        Table sheet = Table.create(modelType);
        return new Reader<>(sheet).from(fromStream);
    }


    public Reader<T> from(File fromFile) {
        if (null == fromFile || !fromFile.exists()) {
            throw new IllegalArgumentException("excel file must be exist");
        }
        this.fromFile = fromFile;
        return this;
    }

    public Reader<T> from(InputStream fromStream) {
        this.fromStream = fromStream;
        return this;
    }

    public Stream<Table<T>> asTableStream() {
        if (this.table == null) {
            throw new IllegalArgumentException("modelType can be not null");
        }

        if (fromFile == null && fromStream == null) {
            throw new IllegalArgumentException("Excel source not is null");
        }

        if (fromFile != null) {
            ReaderFactory.readByFile(this);
            return this.tableStream;
        } else {
            ReaderFactory.readByStream(this);
            return this.tableStream;
        }
    }


    public Stream<T> asStream() {

        this.stream = this.asTableStream().flatMap(sheet -> sheet.data().stream());

        return this.stream;
    }


    public List<T> asList() throws ReaderException {
        Stream<T> stream = this.asStream();
        return stream.collect(toList());
    }

    public InputStream fromStream() {
        return this.fromStream;
    }

    public File fromFile() {
        return fromFile;
    }

    public Charset charset(){
        return this.charset;
    }

    public Reader<T> table(Table table) {
        this.table = table;
        return this;
    }

    public Table table() {
        return this.table;
    }

    public Stream<T> stream() {
        return this.stream;
    }

    public Stream<Table<T>> tableStream() {
        return this.tableStream;
    }

    public void tableStream(Stream<Table<T>> tableStream) {
        this.tableStream = tableStream;
    }

    public void stream(Stream<T> stream) {
        this.stream = stream;
    }


}
