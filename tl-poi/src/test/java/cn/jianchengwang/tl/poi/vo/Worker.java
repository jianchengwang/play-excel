package cn.jianchengwang.tl.poi.vo;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wjc on 2019/9/2
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @ExcelColumn(header = "姓名", index = 3)
    private String name;
    @ExcelColumn(header = "工作年限", index = 2)
    private String workYear;
    @ExcelColumn(header = "职称", index = 1)
    private String jobTitle;
    @ExcelColumn(header = "部门", index = 0, groupBy = true)
    private String department;
    private String selfRemark;
    private String projectRemark;
}
