package cn.jianchengwang.tl.poi.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by wjc on 2019/9/4
 **/
public class Const {

    public static final String DEFAULT_SHEET_NAME = "Sheet0";
    public static final String DEFAULT_TEMPLATE_SHEET_NAME = "Sheet";

    // 内存JXLS参数
    public interface JXLS_TAGPARAM {
        final String HEADERS = "headers"; // jx:grid headers
        final String PROPS = "props"; // jx:grid props
        final String DATA = "data"; // jx:grid data

        final String ITEMS = "items"; // jx:each items
        final String SHEETNAMES = "sheetNames"; // jx:each multisheet="sheetNames"
    }

    // 字符编码
    public static Charset charset = StandardCharsets.UTF_8;
    public static String DEFAULT_FONT_NAME    = "SimHei";
    public static int    DEFAULT_COLUMN_WIDTH = 20 * 256;

    // 样式
    public interface  DEFAULT_STYLE {
        static CellStyle defaultTitleStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 20);
            font.setBold(true);
            font.setFontName(DEFAULT_FONT_NAME);
            style.setFont(font);
            return style;
        }

        /**
         * The default Excel header style.
         *
         * @param workbook Excel workbook
         * @return header row cell style
         */
        static CellStyle defaultHeaderStyle(Workbook workbook) {
            CellStyle headerStyle = workbook.createCellStyle();

            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setFontHeightInPoints(Short.parseShort("16"));
            font.setBold(true);
            font.setFontName(DEFAULT_FONT_NAME);
            headerStyle.setFont(font);
            return headerStyle;
        }

        /**
         * The default Excel column style.
         *
         * @param workbook Excel workbook
         * @return row column cell style
         */
        static CellStyle defaultColumnStyle(Workbook workbook) {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setWrapText(true);

            cellStyle.setDataFormat((short) 0);

            Font font = workbook.createFont();
            font.setFontName(DEFAULT_FONT_NAME);
            font.setFontHeightInPoints(Short.parseShort("14"));

            cellStyle.setFont(font);
            return cellStyle;
        }
    }
}
