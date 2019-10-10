package cn.jianchengwang.tl.poi;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.gridSheet.StudentGridSheet;
import cn.jianchengwang.tl.poi.vo.BasicForm;
import cn.jianchengwang.tl.poi.vo.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by wjc on 2019/9/3
 **/
public class WriterJxlsTest {

    private long beginTime = 0;

    @Before
    public void before() {
       beginTime = System.currentTimeMillis();
    }

    @After
    public void after() {
        System.out.println((System.currentTimeMillis() - beginTime)/1000);
    }

    final String TEMPLATE_DIR = WriterJxlsTest.class.getResource("/").toString().replace("file:/", "") + "templates";
    final String OUT_DIR = WriterJxlsTest.class.getResource("/").toString().replace("file:/", "") + "out";
    List<Worker> workerList = new ArrayList<>();
    List<StudentGridSheet> studentGridSheetList = new ArrayList<>();
    {

        workerList = DataGen.genWorkerList(100000);

        GridSheet gridSheet1 = GridSheet.build().sheetName("c1").data(DataGen.genStudentList(100));
        GridSheet gridSheet2 = GridSheet.build().sheetName("c2").data(DataGen.genStudentList(100));
        studentGridSheetList.add(new StudentGridSheet(gridSheet1, "c1", "aa", "bb"));
        studentGridSheetList.add(new StudentGridSheet(gridSheet2, "c2", "cc", "dd"));
    }

    @Test
    public void testSimpleHello() throws Exception {
        //模板文件
        InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/simpleHello.xls");
        OutputStream os = new FileOutputStream(new File(OUT_DIR + "/simpleHello.xls"));
        Writer.create()
                .template(is)
                .putVar("name", "world")
                .out(os);
    }

    @Test
    public void testSimpleEach() throws Exception {
        //模板文件
        InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/simpleEach.xls");
        OutputStream os = new FileOutputStream(new File(OUT_DIR + "/simpleEach.xls"));

        //添加A-Z的字符数据
        List<Character> dataList = new ArrayList<Character>();
        for(int i=65; i <= 90 ; i++){
            dataList.add((char)i);
        }
        //数组循环不支持？
        List<Object[]> arrays = new ArrayList<Object[]>();
        Object a1[] = new Object[]{"hh1" , "男" , 25};
        Object a2[] = new Object[]{"hh2" , "男" , 26};
        Object a3[] = new Object[]{"hh3" , "男" , 27};
        arrays.add(a1);
        arrays.add(a2);
        arrays.add(a3);
        //集合循环
        List<List<Object>> list = new ArrayList<List<Object>>();
        list.add(Arrays.asList(a1));
        list.add(Arrays.asList(a2));
        list.add(Arrays.asList(a3));

        Writer.create()
                .template(is)
                .putVar("workers", workerList)
                .putVar("title", "工作人员表格")
                .putVar("arrays", arrays)
                .putVar("list", list)
                .putVar("dataList", dataList)
                .out(os);
    }

    @Test
    public void testSimpleGrid() throws Exception {
        InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/simpleGrid.xls");
        File outFile = new File(OUT_DIR + "/simpleGrid.xls");
        Writer.create()
                .template(is)
                .putVar("headers", Arrays.asList("姓名" , "工作年限" , "职称"))
                .putVar("data", workerList)
//                .putVar("props", "name,workYear,jobTitle") // test String props
//                .putVar("props", Arrays.asList("name", "workYear", "jobTitle")) // test List props
                .putVar("props", new String[]{"name", "workYear", "jobTitle"}) // test String[] props
                .out(outFile);
    }

    @Test
    public void testSimpleMultiGrid() throws Exception {
        InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/simpleMultiGrid.xlsx");
        File outFile = new File(OUT_DIR + "/simpleMultiGrid.xlsx");
        Writer.create()
                .template(is)
                .enableAutoGroupBy(true)
                .addSheetBuilder(Worker.class, workerList)
                .removeSheet("Sheet1")
                .out(outFile);
    }

   @Test
    public void testStudentGrid() throws WriterException {
       InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/student.xls");
       File outFile = new File(OUT_DIR + "/student.xls");
       Writer.create().template(is)
               .addSheetBuilderList(studentGridSheetList)
               .removeSheet("template")
               .out(outFile);
   }

    @Test
    public void testSimpleObjectFormGrid() throws WriterException {
        InputStream is = WriterJxlsTest.class.getClass().getResourceAsStream("/templates/simpleObjectForm.xls");
        File outFile = new File(OUT_DIR + "/simpleObjectForm.xls");
        BasicForm basicForm = new BasicForm("银河救护队", "超能部门", UUID.randomUUID().toString(), "2019-09-15");
        Writer.create()
                .template(is)
                .putVar("basicform", basicForm)
                .out(outFile);
    }
}
