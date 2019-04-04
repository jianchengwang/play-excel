package cn.jianchengwang.playexcel.test.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.test.BaseTest;
import cn.jianchengwang.playexcel.test.model.Book;
import cn.jianchengwang.playexcel.test.model.Sample;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReaderFactoryTest extends BaseTest {

    private final String EXCELPATH = "/home/wjc/ext/workspace/IdeaProjects/play-excel/out/test/resources/";

    @Test
    public void testReadByFileXLSX() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);

        reader.from(new File(EXCELPATH + "/SampleData.xlsx"));

        Stream<Sample> stream  = reader.asStream();
        List<Sample>   samples = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(samples);
        assertEquals(43, samples.size());
    }


    @Test
    public void testReadByFileXLS() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);
        reader.sheet().extMsgConfig(3, 2);

        reader.from(new File(EXCELPATH + "/SampleData.xls"));

        Stream<Sample> stream  = reader.asStream();
        List<Sample>   samples = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(samples);
        assertEquals(43, samples.size());
    }


    @Test
    public void testReadByFileCSV() throws ReaderException {
        Reader<Book> reader = Reader.create(Book.class);
        reader.from(new File(EXCELPATH + "book.csv"));

        Stream<Book> stream  = reader.asStream();
        List<Book> samples = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(samples);
        assertEquals(5, samples.size());
    }
}
