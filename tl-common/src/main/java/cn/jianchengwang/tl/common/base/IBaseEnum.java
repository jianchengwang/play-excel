package cn.jianchengwang.tl.common.base;

import java.io.Serializable;

/**
 * Created by wjc on 2019/10/10
 **/
public interface IBaseEnum<T extends Serializable> {
    T getValue();
    Object getDescription();
}