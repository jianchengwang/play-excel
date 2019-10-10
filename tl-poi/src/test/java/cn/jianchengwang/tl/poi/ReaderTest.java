package cn.jianchengwang.tl.poi;

import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.vo.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.stream.Stream;

/**
 * Created by wjc on 2019/9/6
 **/
public class ReaderTest {

    private long beginTime = 0;

    @Before
    public void before() {
        beginTime = System.currentTimeMillis();
    }

    @After
    public void after() {
        System.out.println((System.currentTimeMillis() - beginTime)/1000);
    }

    final String OUT_DIR = WriterJxlsTest.class.getResource("/").toString().replace("file:/", "") + "out";

    @Test
    public void readSimpleGridXls() {

        File fromFile = new File(OUT_DIR + "/defaultSimpleGrid.xls");
        Reader<Worker> reader = Reader.create(GridSheet.build().clazz(Worker.class).sheetIndex(0).startRow(1).headLineRow(1));
        Stream<Worker> stream = reader.from(fromFile).asStream();
        stream.forEach(worker -> {
            System.out.println(worker.getName());
        });
    }

    @Test
    public void readMultiGridXLSX() {

        File fromFile = new File(OUT_DIR + "/defaultMultiGrid.xlsx");
        Reader<Worker> reader = Reader.create(GridSheet.build().clazz(Worker.class).startRow(1).headLineRow(1));
        Stream<GridSheet<Worker>> stream = reader.from(fromFile).asGridSheetStream();
        stream.forEach(gridSheet -> {
            System.out.println(gridSheet.getSheetName());
        });
    }
}
