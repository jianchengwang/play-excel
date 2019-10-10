package cn.jianchengwang.tl.storage.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wjc on 2019/8/27
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListObject {
    private String prefix;
    private String nextMarker;
    private Integer limit;
    List<SObject> objectList;
    String[] commonPrefixes;
}
