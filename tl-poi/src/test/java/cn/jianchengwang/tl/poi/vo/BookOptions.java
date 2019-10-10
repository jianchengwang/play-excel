package cn.jianchengwang.tl.poi.vo;

import cn.jianchengwang.tl.poi.excel.config.option.Options;

/**
 * Created by wjc on 2019/9/6
 **/
public class BookOptions implements Options {
    @Override
    public String[] get() {
        return new String[] {"book1", "book2", "book3", "book4", "book5"};
    }
}