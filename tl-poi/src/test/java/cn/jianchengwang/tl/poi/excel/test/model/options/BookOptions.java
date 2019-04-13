package cn.jianchengwang.tl.poi.excel.test.model.options;

import cn.jianchengwang.tl.poi.excel.config.options.Options;

public class BookOptions implements Options {
    @Override
    public String[] get() {
        return new String[] {"book1", "book2", "book3", "book4", "book5"};
    }
}
