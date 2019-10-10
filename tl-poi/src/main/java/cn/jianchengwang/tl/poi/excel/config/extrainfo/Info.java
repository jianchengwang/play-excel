package cn.jianchengwang.tl.poi.excel.config.extrainfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wjc on 2019/9/6
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Info {
    private String k; // 附加信息key，比如 姓名:hello  里的姓名:
    private String v; // 附加信息val, 比如 姓名:hello 里的hello
    private String comment; // 附加信息的注释
}
