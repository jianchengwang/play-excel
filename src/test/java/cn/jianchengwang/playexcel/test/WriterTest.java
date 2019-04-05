package cn.jianchengwang.playexcel.test;

import cn.jianchengwang.playexcel.Writer;
import cn.jianchengwang.playexcel.config.Table;
import cn.jianchengwang.playexcel.config.extmsg.ExtMsg;
import cn.jianchengwang.playexcel.exception.WriterException;
import cn.jianchengwang.playexcel.test.model.PerformanceTestModel;
import cn.jianchengwang.playexcel.test.model.Sample;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WriterTest extends BaseTest {

    private final String EXCELPATH = "/home/wjc/ext/workspace/IdeaProjects/play-excel/out/test/resources/";

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
        String fileName = "write_mul_test.xlsx";

        Writer.create()
                .witchTable(Table.create(PerformanceTestModel.class,0,"sheet1")
                        .headTitle("sheet1")
                        .data(readyData())
                        .extMsgList(buildExtMsg()))
                .witchTable(Table.create(Sample.class, 1, "sheet2").headTitle("sheet2").data(buildData()))
                .to(new File(EXCELPATH + fileName));
        System.out.println((System.currentTimeMillis() - beginTime)/1000);
//        deleteTempFile(fileName);
    }
}
