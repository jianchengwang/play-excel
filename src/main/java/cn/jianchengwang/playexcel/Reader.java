package cn.jianchengwang.playexcel;

import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.metadata.SheetMd;
import cn.jianchengwang.playexcel.reader.ReaderFactory;

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

    private SheetMd<T> sheet;

    public Reader(SheetMd<T> sheet) {
        this.sheet = sheet;
    }

    public static <T> Reader<T> create(Class<T> modelType) {
        SheetMd sheet = SheetMd.create(modelType);
        return new Reader<>(sheet);
    }

    public static <T> Reader<T> create(Class<T> modelType, File fromFile) {
        SheetMd sheet = SheetMd.create(modelType);
        return new Reader<>(sheet).from(fromFile);
    }

    public static <T> Reader<T> create(Class<T> modelType, InputStream fromStream) {
        SheetMd sheet = SheetMd.create(modelType);
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

    public Stream<T> asStream() {
        if (this.sheet == null) {
            throw new IllegalArgumentException("modelType can be not null");
        }

        if (fromFile == null && fromStream == null) {
            throw new IllegalArgumentException("Excel source not is null");
        }

        if (fromFile != null) {
            return ReaderFactory.readByFile(this);
        } else {
            return ReaderFactory.readByStream(this);
        }
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

    public SheetMd sheet() {
        return this.sheet;
    }

}
