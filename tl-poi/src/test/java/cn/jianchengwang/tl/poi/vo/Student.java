package cn.jianchengwang.tl.poi.vo;

import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	@ExcelColumn(header = "ID", index = 0)
	String id;
	@ExcelColumn(header = "姓名", index = 1)
	String name;
	@ExcelColumn(header = "语文", index = 2)
	Integer chinese;
	@ExcelColumn(header = "数学", index = 3)
	Integer math;
	@ExcelColumn(header = "英语", index = 4)
	Integer english;
	@ExcelColumn(header = "政治", index = 5)
	Integer politics;
	@ExcelColumn(header = "历史", index = 6)
	Integer history;
	@ExcelColumn(header = "生物", index = 7)
	Integer geography;
}
