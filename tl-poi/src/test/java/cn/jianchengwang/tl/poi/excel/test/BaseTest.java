package cn.jianchengwang.tl.poi.excel.test;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.test.model.MultiHeaderBook;
import cn.jianchengwang.tl.poi.excel.test.model.PerformanceTestModel;
import cn.jianchengwang.tl.poi.excel.test.model.Sample;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class BaseTest {

    protected final int testCount = 1_0000;
//        protected final int    testCount    = 100_0000;

    protected final String testFileName = "test_write_100w_rows.xlsx";

    protected List<Sample> buildData() {
        List<Sample> samples = new ArrayList<>();
        samples.add(new Sample(LocalDate.now(), "hello01", 101));
        samples.add(new Sample(LocalDate.now(), "数据项02", 102));
        samples.add(new Sample(LocalDate.now(), "数据项03", 103));
        samples.add(new Sample(LocalDate.now(), "数据项04", 104));
        samples.add(new Sample(LocalDate.now(), "hello05", 105));
        return samples;
    }

    protected List<MultiHeaderBook> buildMultiHeaderBook() {
        List<MultiHeaderBook> books = new ArrayList<>();
        books.add(new MultiHeaderBook("新名字的故事", "埃莱娜·费兰特", 59.0, LocalDate.now()));
        books.add(new MultiHeaderBook("不可思议的朋友", "哈哈哈", 79.0, LocalDate.now()));
        books.add(new MultiHeaderBook("明月泪", "猫九大大", 29.0, LocalDate.now()));
        books.add(new MultiHeaderBook("温柔刀", "猫九大大", 49.0, LocalDate.now()));
        books.add(new MultiHeaderBook("端到草席", "猫九大大", 99.0, LocalDate.now()));
        return books;
    }

    protected List<PerformanceTestModel> readyData() {

        List<PerformanceTestModel> data = new ArrayList<>(testCount);

        LocalDate now    = LocalDate.now();
        Random random = new Random();

        for (int i = 1; i <= testCount; i++) {
            data.add(new PerformanceTestModel(i, UUID.randomUUID().toString(), now,
                    new BigDecimal(String.valueOf(random.nextDouble() * 1000)).setScale(2, 0).doubleValue(),
                    new BigDecimal(String.valueOf(random.nextDouble() * 10000)).setScale(3, 0),
                    "15800001112", (byte) (i % 3))
            );
        }
        return data;
    }

    protected List<ExtMsg> buildExtMsg() {
        List<ExtMsg> extMsgList = new ArrayList<>();
        extMsgList.add(new ExtMsg("name", "haha"));
        extMsgList.add(new ExtMsg("email", "haha@email.com"));
        extMsgList.add(new ExtMsg("age", "100"));
        extMsgList.add(new ExtMsg("home", "maybe"));
        extMsgList.add(new ExtMsg("love", "张巧霞"));

        return extMsgList;
    }

    protected void writeTestExcel(List<PerformanceTestModel> rows) throws WriterException {
        log.info("data  ready !!!");
        log.info("start write !!!");

        Writer.create()
                .withRows(rows)
                .headerTitle("Test Write Model Excel")
                .to(new File(testFileName));
    }

    protected String classPath() {
        return BaseTest.class.getResource("/").getPath();
    }

    protected void deleteTempFile(String fileName) {
        try {
            Files.delete(Paths.get(fileName));
        } catch (IOException e) {
            log.warn("delete file {} fail", fileName, e);
        }
    }
}
