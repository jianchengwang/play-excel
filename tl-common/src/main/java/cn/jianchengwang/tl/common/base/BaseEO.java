package cn.jianchengwang.tl.common.base;


import lombok.Data;

/**
 * 基础EO -> Excel Object
 * Created by wjc on 2019/9/17
 **/
@Data
public class BaseEO {
    private boolean success = true; // 成功失败
    private String errorMsg = ""; // 错误信息
}