package cn.jianchengwang.playexcel.test.model;

import cn.jianchengwang.playexcel.annotation.ExcelColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Sample {

    @ExcelColumn(title = "日期", index = 0, format = "M/d/yy")
    private LocalDate date;

    @ExcelColumn(title = "地区", index = 1)
    private String location;

    @ExcelColumn(title = "proportion", index = 4)
    private int proportion;

    @ExcelColumn(title = "ss", index = 5)
    private double ss;

    @ExcelColumn(title = "余额", index = 6)
    private BigDecimal amount;

    public Sample() {
    }

    public Sample(LocalDate date, String location, int proportion) {
        this.date = date;
        this.location = location;
        this.proportion = proportion;
    }

}
