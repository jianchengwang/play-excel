package cn.jianchengwang.tl.poi;

import cn.jianchengwang.tl.poi.excel.config.extrainfo.Info;
import cn.jianchengwang.tl.poi.vo.Book;
import cn.jianchengwang.tl.poi.vo.BookOptions;
import cn.jianchengwang.tl.poi.vo.Student;
import cn.jianchengwang.tl.poi.vo.Worker;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by wjc on 2019/9/6
 **/
public class DataGen {

    static Random random = new Random();

    public static List<Worker> genWorkerList(int number) {
        List<Worker> workerList = new ArrayList<>();
        List<String> departList = Arrays.asList("产品", "研发", "市场调研", "销售");
        IntStream.rangeClosed(1,number).forEach(i -> {
            workerList.add(new Worker(UUID.randomUUID().toString(), i + "", "java" + i, departList.get(random.nextInt(departList.size())), "", ""));
        });
        return workerList;
    }

    public static List<Student> genStudentList(int number) {
        List<Student> studentList = new ArrayList<>();
        IntStream.rangeClosed(1,number).forEach(i -> {
            studentList.add(new Student(UUID.randomUUID().toString(), "name" + i,
                    random.nextInt(100), random.nextInt(100), random.nextInt(100),
                    random.nextInt(100), random.nextInt(100), random.nextInt(100)
            ));
        });
        return studentList;
    }

    public static List<Info> genExtraInfoList(int number) {
        List<Info> infoList = new ArrayList<>();
        List<String> kList = Arrays.asList("a", "b", "c", "d", "e", "1", "2", "3", "hello", "你好");
        IntStream.rangeClosed(1, number).forEach(i -> {
            infoList.add(new Info(
                    kList.get(random.nextInt(kList.size())),
                    kList.get(random.nextInt(kList.size()))  + i,
                    "注释" + i));
        });
        return infoList;
    }

    public static List<Book> genBookList(int number) {
        List<Book> bookList = new ArrayList<>();
        String[] booksNames = new BookOptions().get();
        IntStream.rangeClosed(1, number).forEach(i -> {
            bookList.add(new Book(booksNames[random.nextInt(booksNames.length)], "author"+i, 100d, LocalDate.now()));
        });
        return bookList;
    }
}
