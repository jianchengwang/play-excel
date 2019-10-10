package cn.jianchengwang.tl.poi;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.ExtraInfo;
import cn.jianchengwang.tl.poi.excel.enums.ExcelType;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.vo.Book;
import cn.jianchengwang.tl.poi.vo.Student;
import cn.jianchengwang.tl.poi.vo.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by wjc on 2019/9/6
 **/
public class WriterTest {

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
    public void testCsv() throws WriterException {
        File outFile = new File(OUT_DIR + "/testCSV.csv");
        Writer.create(ExcelType.CSV)
                .addSheetBuilder(GridSheet.build().data(DataGen.genWorkerList(1000000))) // 100万/5s
                .out(outFile);
    }

    @Test
    public void testXls() throws WriterException {
        File outFile = new File(OUT_DIR + "/testXlS.xls");
        Writer.create(ExcelType.XLS)
                .addSheetBuilder(GridSheet.build().data(DataGen.genWorkerList(60000))) // 6万/2s
                .out(outFile);
    }

    @Test
    public void testXlsx() throws WriterException {
        File outFile = new File(OUT_DIR + "/testXlSX.xlsx");
        Writer.create()
                .addSheetBuilder(GridSheet.build().data(DataGen.genWorkerList(1000000))) // 100万/19s
                .out(outFile);
    }

    @Test
    public void testMultiSheetXls() throws WriterException {
        File outFile = new File(OUT_DIR + "/testMultiSheetXlS.xls"); // 6万/2s
        Writer.create(ExcelType.XLS)
                .enableAutoGroupBy(true)
                .addSheetBuilder(Worker.class, DataGen.genWorkerList(60000))
                .out(outFile);
    }

    @Test
    public void testMultiSheetXlsx() throws WriterException {
        File outFile = new File(OUT_DIR + "/testMultiSheetXlSX.xlsx"); // 100万/20s
        Writer.create()
                .enableAutoGroupBy(true)
                .addSheetBuilder(Worker.class, DataGen.genWorkerList(1000000))
                .out(outFile);
    }

    @Test
    public void testExtraInfo() {
        File outFile = new File(OUT_DIR + "/testExtraInfoXlS.xls");
        Writer.create(ExcelType.XLS)
                .addSheetBuilder(GridSheet.build()
                        .data(DataGen.genWorkerList(10000))
                        .extraInfo(ExtraInfo.create(DataGen.genExtraInfoList(5), 2))
                )
                .out(outFile);
    }

    @Test
    public void testExtraInfoAndMultiSheet() {
        File outFile = new File(OUT_DIR + "/testExtraInfoAndMultiSheet.xlsx");
        Writer.create(ExcelType.XLSX)
                .addSheetBuilder(GridSheet.build().sheetName("职员列表").clazz(Worker.class).data(DataGen.genWorkerList(10000)).extraInfo(ExtraInfo.create(DataGen.genExtraInfoList(3), 1)))
                .addSheetBuilder(GridSheet.build().sheetName("学生列表").clazz(Student.class).data(DataGen.genStudentList(10000)).extraInfo(ExtraInfo.create(DataGen.genExtraInfoList(4), 2)))
                .out(outFile);
    }

    @Test
    public void testOptionsAndComment() {
        File outFile = new File(OUT_DIR + "/testOptionsAndComment.xlsx");
        Writer.create()
                .addSheetBuilder(GridSheet.build().sheetName("图书列表").clazz(Book.class).data(DataGen.genBookList(10000)))
                .out(outFile);
    }

}
