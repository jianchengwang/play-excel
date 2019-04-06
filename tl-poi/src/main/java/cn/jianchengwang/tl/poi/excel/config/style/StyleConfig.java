package cn.jianchengwang.tl.poi.excel.config.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class StyleConfig {

    private StyleConsumer<Workbook, CellStyle> titleStyle;
    private StyleConsumer<Workbook, CellStyle> headerStyle;
    private StyleConsumer<Workbook, CellStyle> cellStyle;

    public StyleConfig() {
    }

    public StyleConfig(StyleConsumer<Workbook, CellStyle> titleStyle) {
        this.titleStyle = titleStyle;
    }


    public StyleConfig(StyleConsumer<Workbook, CellStyle> titleStyle, StyleConsumer<Workbook, CellStyle> headerStyle) {
        this.titleStyle = titleStyle;
        this.headerStyle = headerStyle;
    }


    public StyleConfig(StyleConsumer<Workbook, CellStyle> titleStyle, StyleConsumer<Workbook, CellStyle> headerStyle, StyleConsumer<Workbook, CellStyle> cellStyle) {
        this.titleStyle = titleStyle;
        this.headerStyle = headerStyle;
        this.cellStyle = cellStyle;
    }

    public static StyleConfig create(StyleConsumer<Workbook, CellStyle> titleStyle) {

        return new StyleConfig(titleStyle);
    }


    public static StyleConfig create(StyleConsumer<Workbook, CellStyle> titleStyle, StyleConsumer<Workbook, CellStyle> headerStyle) {
        return new StyleConfig(titleStyle, headerStyle);
    }


    public static StyleConfig create(StyleConsumer<Workbook, CellStyle> titleStyle, StyleConsumer<Workbook, CellStyle> headerStyle, StyleConsumer<Workbook, CellStyle> cellStyle) {
       return new StyleConfig(titleStyle, headerStyle, cellStyle);
    }

    public StyleConsumer<Workbook, CellStyle> titleStyle() {
        return this.titleStyle;
    }

    public StyleConsumer<Workbook, CellStyle> headerStyle() {
        return this.headerStyle;
    }

    public StyleConsumer<Workbook, CellStyle> cellStyle() {
        return this.cellStyle;
    }
}
