package cn.jianchengwang.tl.poi.gridSheet;

import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.vo.Student;
import lombok.Data;

/**
 * Created by wjc on 2019/9/4
 **/
@Data
public class StudentGridSheet extends GridSheet<Student> {

    private String className;
    private String teacherComment;
    private String directorComment;

    public StudentGridSheet(GridSheet gridSheet, String className, String teacherComment, String directorComment) {
        super(gridSheet);
        this.className = className;
        this.teacherComment = teacherComment;
        this.directorComment = directorComment;
    }
}
