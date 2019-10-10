package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.poi.excel.Const;
import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import lombok.Data;
import org.jxls.area.Area;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.CellRef;
import org.jxls.formula.FastFormulaProcessor;
import org.jxls.formula.FormulaProcessor;
import org.jxls.formula.StandardFormulaProcessor;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import java.util.*;
import java.util.stream.Collectors;

import cn.jianchengwang.tl.poi.excel.writer.command.*;

/**
 * 借鉴jxlss
 * https://gitee.com/lnkToKing/jxlss
 * jxls官方文档demo
 * http://jxls.sourceforge.net/samples
 * https://bitbucket.org/leonate/jxls-demo/src/master/
 *
 * Created by wjc on 2019/9/3
 **/
@Data
public class JxlsBuilder {

    static {
        //注册 jx 命令
        XlsCommentAreaBuilder.addCommandMapping("grid", GridCommand.class);
    }
    private Writer writer; // 通用写接口

    private JxlsHelper jxlsHelper = JxlsHelper.getInstance();
    private Transformer transformer;

    public JxlsBuilder(Writer writer) {
        this.writer = writer;
    }

    private Transformer getTransformer() {

        if (transformer == null) {
            transformer = jxlsHelper.createTransformer(writer.getTemplate(), writer.getOut());
        }
        return transformer;
    }

    private void transform() {

        boolean isMultiGrid = writer.isMultiGrid();
        List<GridSheet> gridSheetList = writer.getGridSheetList();
        String[] removeSheetNames = writer.getRemoveSheetNames();

        try {
            if(gridSheetList !=null && !gridSheetList.isEmpty()) {
                GridSheet gridSheet = gridSheetList.get(0);
                writer.putVar(Const.JXLS_TAGPARAM.HEADERS, gridSheet.getHeaders());
                writer.putVar(Const.JXLS_TAGPARAM.PROPS, gridSheet.getProps());

                if(isMultiGrid && gridSheetList.size()>1) {
                    writer.putVar(Const.JXLS_TAGPARAM.ITEMS, gridSheetList);
                    writer.putVar(Const.JXLS_TAGPARAM.SHEETNAMES, gridSheetList.stream().map(gsb -> gsb.getSheetName()).collect(Collectors.toList()));
                } else {
                    writer.putVar(Const.JXLS_TAGPARAM.DATA, gridSheet.getData());
                }
            }

            jxlsHelper.getAreaBuilder().setTransformer(transformer);
            List<Area> xlsAreaList = jxlsHelper.getAreaBuilder().build();
            xlsAreaList.forEach(xlsArea -> {
                xlsArea.applyAt(new CellRef(xlsArea.getStartCellRef().getCellName()), writer.getContext());
                if (jxlsHelper.isProcessFormulas()) {
                    FormulaProcessor fp;
                    if(jxlsHelper.isUseFastFormulaProcessor()){
                        fp = new FastFormulaProcessor();
                    }else{
                        fp = new StandardFormulaProcessor();
                    }
                    xlsArea.setFormulaProcessor(fp);
                    xlsArea.processFormulas();
                }
            });

            if(removeSheetNames!=null && removeSheetNames.length>0) {
                Arrays.stream(removeSheetNames).forEach(sheetName -> {
                    transformer.deleteSheet(sheetName);
                });
            }
            if(isMultiGrid) {
                transformer.deleteSheet(Const.DEFAULT_TEMPLATE_SHEET_NAME); // delete template sheet
            }

            transformer.write();
        } catch (Exception e) {
            throw E.unexpected(e);
        }

    }

    public void build() {
        getTransformer();
        transform();
    }
}
