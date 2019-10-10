package cn.jianchengwang.tl.poi.excel.writer.command;

import cn.jianchengwang.tl.poi.excel.Const;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.common.Size;

import java.util.List;

/**
 * 动态设置grid props
 * Created by wjc on 2019/9/4
 **/
public class GridCommand extends org.jxls.command.GridCommand {

    private final String PROPS = Const.JXLS_TAGPARAM.PROPS;

    @Override
    public Size applyAt(CellRef cellRef, Context context) {

        // 如果有设置grid props则自动设置到命令中
        if(context.getVar(PROPS) != null) {
            String props = "";
            Object value = context.getVar(PROPS);

            if(value instanceof List) {
                props = String.join(",", (List) value);
            } else if(value instanceof String[]) {
                props = String.join(",", (String[]) value);
            } else if(value instanceof String) {
                props = (String) value;
            } else {
                throw new IllegalArgumentException("props 属性只支持 String,String[],List<String>");
            }
            setProps(props);
        }
        return super.applyAt(cellRef, context);
    }
}
