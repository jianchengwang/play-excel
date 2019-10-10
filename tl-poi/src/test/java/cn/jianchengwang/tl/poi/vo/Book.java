package cn.jianchengwang.tl.poi.vo;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by wjc on 2019/9/6
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @ExcelColumn(header = "书名", index = 0, options = BookOptions.class, comment = "你好，请从下拉列表选择数据")
    private String title;

    @ExcelColumn(header = "作者", index = 1, comment = "你好，我是注释")
    private String author;

    @ExcelColumn(header = "售价", index = 2)
    private Double price;

    @ExcelColumn(header = "出版日期", index = 3, dateFormat = "yyyy年M月")
    private LocalDate publishDate;

}