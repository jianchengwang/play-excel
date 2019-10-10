package cn.jianchengwang.tl.poi.excel.config;

import lombok.Builder;
import lombok.Data;

/**
 * Created by wjc on 2019/9/4
 **/
@Data
@Builder
public class GridColumn {
    private String header;
    private String prop;
    private Integer index;
}
