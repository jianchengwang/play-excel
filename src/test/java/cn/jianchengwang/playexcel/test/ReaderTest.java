package cn.jianchengwang.playexcel.test;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.config.Table;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.test.model.Book;
import cn.jianchengwang.playexcel.test.model.PerformanceTestModel;
import cn.jianchengwang.playexcel.test.model.Sample;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReaderTest extends BaseTest {

    private final String EXCELPATH = "/home/wjc/ext/workspace/IdeaProjects/play-excel/out/test/resources/";

    @Test
    public void testReadByFileXLSX() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);

        reader.from(new File(EXCELPATH + "/SampleData.xlsx"));

        Stream<Sample> stream  = reader.asStream();
        List<Sample>   samples = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(samples);
        assertEquals(86, samples.size());
    }

    @Test
    public void testReadByFileXLSX0() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);
        reader.table().extMsgConfig(3, 2);

        reader.from(new File(EXCELPATH + "/SampleData0.xlsx"));

        Stream<Table<Sample>> stream  = reader.asTableStream();
        List<Table<Sample>> tables = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(tables);
        assertEquals(1, tables.size());
    }


    @Test
    public void testReadByFileXLS() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);

        reader.from(new File(EXCELPATH + "/SampleData.xls"));

        Stream<Sample> stream  = reader.asStream();
        List<Sample>   samples = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(samples);
        assertEquals(43, samples.size());
    }


    @Test
    public void testReadByFileXLS0() throws ReaderException {
        Reader<Sample> reader = Reader.create(Sample.class);
        reader.table().extMsgConfig(3, 2);

        reader.from(new File(EXCELPATH + "/SampleData0.xls"));

        Stream<Table<Sample>> stream  = reader.asTableStream();
        List<Table<Sample>> tables = stream.collect(Collectors.toList());

        assertNotNull(stream);
        assertNotNull(tables);
        assertEquals(2, tables.size());
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

    @Test
    public void testBigRead() {

        Long beginTime = System.currentTimeMillis();

        Reader<PerformanceTestModel> reader = Reader.create(PerformanceTestModel.class);

        reader.from(new File(EXCELPATH + "/write_test.xlsx"));

        Stream<PerformanceTestModel> stream  = reader.asStream();
        List<PerformanceTestModel>   performanceTestModels = stream.collect(Collectors.toList());

        System.out.println((System.currentTimeMillis() - beginTime)/1000);

        assertNotNull(stream);
        assertNotNull(performanceTestModels);
        assertEquals(10000, performanceTestModels.size());


    }
}
