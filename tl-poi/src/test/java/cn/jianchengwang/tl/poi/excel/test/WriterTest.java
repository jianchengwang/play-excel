package cn.jianchengwang.tl.poi.excel.test;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.test.model.MultiHeaderBook;
import cn.jianchengwang.tl.poi.excel.test.model.PerformanceTestModel;
import cn.jianchengwang.tl.poi.excel.test.model.Sample;
import org.junit.Test;

import java.io.File;

public class WriterTest extends BaseTest {

    private final String EXCELPATH = "/home/wjc/ext/workspace/IdeaProjects/tl-lib/tl-poi/out/test/resources/";

    @Test
    public void testWriteCSV() throws WriterException {
        String fileName = "write_csv_test.csv";
        Writer.create().withRows(buildData()).to(new File(EXCELPATH + fileName));
//        deleteTempFile(fileName);
    }

    @Test
    public void testWriteXLS() throws WriterException {
        Long beginTime = System.currentTimeMillis();

        // single sheet
//        String fileName = "write_test.xlsx";
//        Writer.create().withRows(readyData()).to(new File(EXCELPATH + fileName));

        // multi sheet
//        String fileName = "mul_sheet_test.xls";
//        Writer.create()
//                .withTable(Table.create(PerformanceTestModel.class,0,"sheet1")
//                        .headTitle("sheet1")
//                        .data(readyData())
//                        .extMsgList(buildExtMsg(), 1))
//                .withTable(Table.create(Sample.class, 1, "sheet2")
//                        .headTitle("sheet2").data(buildData())
//                        .extMsgList(buildExtMsg(), 2))
//                .to(new File(EXCELPATH + fileName));


        // multi header
        String fileName = "mul_header_test.xls";
        Writer.create()
                .withTable(Table.create(MultiHeaderBook.class,0,"books")
                        .headTitle("books")
                        .headLineRow(2)
                        .data(buildMultiHeaderBook())
                        .extMsgList(buildExtMsg(), 1))
                .withTable(Table.create(Sample.class, 1, "sample")
                        .headTitle("sample").data(buildData())
                        .extMsgList(buildExtMsg(), 2))
                .to(new File(EXCELPATH + fileName));
        System.out.println((System.currentTimeMillis() - beginTime)/1000);
//        deleteTempFile(fileName);
    }
}
