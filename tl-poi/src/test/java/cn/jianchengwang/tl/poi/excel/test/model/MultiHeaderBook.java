package cn.jianchengwang.tl.poi.excel.test.model;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MultiHeaderBook {

    @ExcelColumn(title = {"基本信息", "书名"}, index = 0)
    private String title;

    @ExcelColumn(title = {"基本信息", "作者"}, index = 1)
    private String author;

    @ExcelColumn(title = "售价", index = 2)
    private Double price;

    @ExcelColumn(title = "出版日期", index = 3, format = "yyyy年M月")
    private LocalDate publishDate;

    public MultiHeaderBook() {
    }

    public MultiHeaderBook(String title, String author, Double price, LocalDate publishDate) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishDate = publishDate;
    }

}
