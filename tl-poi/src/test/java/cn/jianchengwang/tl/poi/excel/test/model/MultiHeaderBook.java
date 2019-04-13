package cn.jianchengwang.tl.poi.excel.test.model;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.test.model.options.BookOptions;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MultiHeaderBook {

    @ExcelColumn(title = { "基本信息", "书名"}, index = 0, options = BookOptions.class)
    private String title;

    @ExcelColumn(title = {"基本信息", "作者"}, index = 1, comment = "作者不能为空")
    private String author;

    @ExcelColumn(title = {"附加信息","售价"}, index = 2, comment = "售价不能小于0")
    private Double price;

    @ExcelColumn(title = "出版日期", index = 3, dateFormat = "yyyy年M月")
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
