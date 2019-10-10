package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.common.EnumTool;
import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.excel.Const;
import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.ExtraInfo;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.Info;
import cn.jianchengwang.tl.poi.excel.config.option.Options;
import cn.jianchengwang.tl.poi.excel.config.style.StyleConfig;
import cn.jianchengwang.tl.poi.excel.converter.*;
import cn.jianchengwang.tl.poi.excel.enums.ExcelType;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public abstract class ExcelWriter {

    private int                 rowNum;
    private Map<Integer, Field> fieldIndexes;
    private List<ExcelColumn> columns;

    Workbook workbook;
    OutputStream outputStream;

    private CreationHelper factory;
    private ExcelType excelType;

    ExcelWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    ExcelWriter() {
    }

    void writeSheet(Writer writer) throws WriterException {

        factory = workbook.getCreationHelper();
        excelType = writer.getExcelType();

        Collection<GridSheet> gridSheetList = writer.getGridSheetList();
        for(GridSheet gridSheet : gridSheetList) {
            writeSheet0(writer, gridSheet);
        }

        // write to OutputStream
        try (OutputStream os = outputStream) {
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WriterException("workbook write to OutputStream error", e);
        }

    }

    /**
     * Write data to Excel Sheet
     * <p>
     * 1. create sheet
     * 2. write title(optional)
     * 3. write column header
     * 4. write row
     * 5. write to OutputStream
     *
     * @param writer writer
     * @throws WriterException
     */
    void writeSheet0(Writer writer, GridSheet gridSheet) throws WriterException {

        // create sheet
        Sheet sheet = workbook.createSheet(gridSheet.sheetName());

        // create draw
        Drawing draw = sheet.createDrawingPatriarch();

        // setting styles
        CellStyle headerStyle = Const.DEFAULT_STYLE.defaultHeaderStyle(workbook);
        CellStyle columnStyle = Const.DEFAULT_STYLE.defaultColumnStyle(workbook);
        CellStyle titleStyle  = Const.DEFAULT_STYLE.defaultTitleStyle(workbook);

        StyleConfig styleConfig = gridSheet.styleConfig();
        if(styleConfig != null) {
            if (null != styleConfig.titleStyle()) {
                titleStyle = styleConfig.titleStyle().accept(workbook, titleStyle);
            }
            if (null != styleConfig.headerStyle()) {
                headerStyle = styleConfig.headerStyle().accept(workbook, headerStyle);
            }
            if (null != styleConfig.cellStyle()) {
                columnStyle = styleConfig.cellStyle().accept(workbook, columnStyle);
            }

        }

        if (writer.withRaw()) {
            writer.sheetConsumer().accept(sheet);
        } else {
            // compute the Filed to be written
            Collection<?> rows   = gridSheet.data();
            Field[]       fields = rows.iterator().next().getClass().getDeclaredFields();

            this.fieldIndexes = new HashMap<>(fields.length);
            this.columns = new ArrayList<>();

            for (Field field : fields) {
                ExcelColumn column = field.getAnnotation(ExcelColumn.class);
                if (null != column) {
                    field.setAccessible(true);
                    fieldIndexes.put(column.index(), field);
                    columns.add(column);
                }
            }

            int colRowIndex = 0;

            // write extraInfo
            if(gridSheet.extraInfo()!=null && gridSheet.extraInfo().haveExtraInfo()) {
                this.writeExtraInfo(sheet, draw, gridSheet, headerStyle, columnStyle);

                colRowIndex += (gridSheet.extraInfo().row() + 1);
            }

            this.rowNum = gridSheet.startRow();
            if (this.rowNum == 0) {
                this.rowNum = colRowIndex + gridSheet.headLineRow();
            }

            try {
                // write column header
                this.writeColHeader(sheet, draw, gridSheet, colRowIndex, headerStyle);

                // write rows
                for (Object row : rows) {
                    this.writeRow(sheet, row, columnStyle);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("write row fail", e);
            }
        }
    }

    private void writeHeader(CellStyle cellStyle, Sheet sheet, String title, int maxColIndex) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(50);

        for (int i = 0; i <= maxColIndex; i++) {
            Cell cell = titleRow.createCell(i);
            if (i == 0) {
                cell.setCellValue(title);
            }
            if (null != cellStyle) {
                cell.setCellStyle(cellStyle);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColIndex));
    }

    private void writeExtraInfo(Sheet sheet, Drawing draw, GridSheet gridSheet, CellStyle kStyle, CellStyle vStyle) {

        int startRow = gridSheet.startRow();
        ExtraInfo extraInfo = gridSheet.extraInfo();

        int t = extraInfo.total();
        int r = extraInfo.row();
        int c = extraInfo.col();
        int s = extraInfo.colSpan();
        List<Info> infoList = extraInfo.infoList();

        int index = 0;
        for(int ri=0; ri<r; ri++) {

            int rowIndex = ri + startRow;
            Row row = sheet.createRow(rowIndex);

            for(int ci=0; ci<(2 + s)*c-1; ci++) {

                if(index == infoList.size()) return;

                Info info = infoList.get(index);

                if(ci % (2 + s) == 0) {
                    Cell cell = row.createCell(ci);
                    cell.setCellStyle(kStyle);
                    cell.setCellValue(info.getK());

                    writeColComment(cell, draw, info.getComment(), rowIndex, rowIndex, ci, ci);
                } else if(ci % (2 + s) == 1){
                    Cell cell = row.createCell(ci);
                    cell.setCellStyle(vStyle);
                    cell.setCellValue(info.getV());

                    ci += s;
                    index++;
                }
            }

        }

    }




    @Data
    private class Node {
        private String text; // 文本信息
        private Integer width; //这个单元格应该在Excel中占有的宽度
        private Integer deep; // 单元格深度
        private Map<String, Node> map; // 子节点 map 集合
        private ExcelColumn column; // excel注解

        public Node(String text, Integer width, Integer deep) {
            this.text = text;
            this.width = width;
            this.deep = deep;

            this.map = new LinkedHashMap<>();
        }

        public Node(String text, Integer width) {
            this.text = text;
            this.width = width;

            this.map = new LinkedHashMap<>();
        }
        public Node(String text) {
            this.text = text;
            this.map = new LinkedHashMap<>();
        }
        public Node() {
            this.map = new LinkedHashMap<>();
        }

        // 添加节点
        void add(String[] text, Integer width) {

            Map<String, Node> rootMap = map;

            Node node =  null;
            //读到叶子结点的前一个结点处
            for (int i = 0; i < text.length-1; i++) {
                //逐层目录读取，如果没有get到，就创建一个新的目录
                node = rootMap.get(text[i]);
                if (node == null) {
                    node = new Node(text[i]);
                    rootMap.put(text[i], node);
                }
                //新目录的大小要同步上
                if(node.getWidth() == null) node.setWidth(Const.DEFAULT_COLUMN_WIDTH);
                node.setWidth(node.getWidth() + width);

                // 设置深度
                if(node.getDeep() == null) node.setDeep(i);

                rootMap = node.getMap();
            }
            //此时的rootMap就是叶子结点所在的目录
            rootMap.put(text[text.length - 1], new Node(text[text.length - 1], width, text.length - 1));

            //还要给这个文件的父文件夹设置deep
            if (node != null) {
                node.setDeep(text.length - 2);
            }
        }



        // 得到节点集合
        List<Node> parse(List<ExcelColumn> columns) {
            List<Node> list = new ArrayList<>();
            for (Map.Entry<String, Node> entry : map.entrySet()) {
                //先把自己保存进去
                list.add(entry.getValue());
                //如果该节点的map不是空集合，证明这是一个“文件夹”（根节点）
                //需要把自己add进去的同时，把它的孩子也全部add进去
                if (entry.getValue().getMap() != null && entry.getValue().getMap().size() > 0) {
                    list.addAll(entry.getValue().parse(columns));
                }
            }
            return list;
        }

        // 计算深度
        int getDeepLength() {
            if (map.isEmpty()) {
                return 0;
            }
            List<Integer> list = new ArrayList<>();
            for (Map.Entry<String, Node> entry : map.entrySet()) {
                list.add(entry.getValue().getDeepLength());
            }
            Collections.sort(list);
            return list.get(list.size() - 1) + 1;
        }

        // 获取该结点下的所有叶子节点的数量
        int getChildrenCount() {
            if (map.isEmpty()) {
                return 1; //就自己一个
            }
            int count = 0;
            for (Map.Entry<String, Node> entry : map.entrySet()) {
                count += entry.getValue().getChildrenCount();
            }
            return count;
        }

        List<Node> build(List<ExcelColumn> columns) {

            for(ExcelColumn column: columns) {
                add(new String[] {column.header()}, column.width()>0? column.width(): Const.DEFAULT_COLUMN_WIDTH);
            }

            return parse(columns);

        }
    }
    private void writeColHeader(Sheet sheet, Drawing draw, GridSheet table, int rowIndex, CellStyle headerStyle) throws Exception {

        if(table.headLineRow() == 1) {
            writeSingleHeader(sheet, draw, rowIndex, headerStyle);
        } else {
            writeMultiHeader(sheet, draw, rowIndex, headerStyle);
        }
    }

    private void writeSingleHeader(Sheet sheet, Drawing draw, int rowIndex, CellStyle headerStyle) {
        Row rowHead = sheet.createRow(rowIndex);
        for (ExcelColumn column : columns) {
            Cell cell = rowHead.createCell(column.index());
            if (null != headerStyle) {
                cell.setCellStyle(headerStyle);
            }
            cell.setCellValue(column.header());
            if (column.width() > 0) {
                sheet.setColumnWidth(column.index(), column.width());
            } else {
                sheet.setColumnWidth(column.index(), Const.DEFAULT_COLUMN_WIDTH);
            }

            writeColComment(cell, draw, column.comment(), rowIndex, rowIndex, column.index(), column.index());
        }
    }

    private void writeMultiHeader(Sheet sheet, Drawing draw, int rowIndex, CellStyle headerStyle) {
        // 迭代Map集合，并重构一套“根目录”
        Node root = new Node();
        List<Node> nodes = root.build(columns);

        int rootDeepLength = root.getDeepLength();

        // 创建多个行，并用数组存储
        Row[] rows = new Row[rootDeepLength];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = sheet.createRow(rowIndex + i);
        }

        int columnIndex = 0;
        int colIndex = 0;
        int firstRow = 0;
        int lastRow = 0;
        int firstCol = 0;
        int lastCol = 0;
        for (Node node : nodes) {

            if (node.map.size() == 0) {
                node.column = columns.get(columnIndex++);
            }

            firstRow = 0;
            lastRow = 0;
            firstCol = colIndex;
            lastCol = colIndex;

            //获取该节点的深度
            int deep = node.getDeep();
            System.out.println(deep);
            //从下往上取行，向右创建

            for (int i = 0; i <= deep; i++) {

                if(rows[i].getCell(colIndex) == null) {
                    rows[i].createCell(colIndex);

                    if(firstRow == 0) firstRow = i;
                    lastRow = i;
                }
            }
            Cell cell = rows[deep].getCell(colIndex);
            cell.setCellValue(node.getText());

            if(node.map.size() > 0) {
                colIndex += node.map.size() - 1;
                lastCol = colIndex;

            } else {
                colIndex++;

                if(node.deep> 0 && node.getChildrenCount() == 1) {
                    lastRow = rootDeepLength - node.getDeep();

                } else {
                    lastRow = rootDeepLength - node.getDeep() - node.getChildrenCount();
                }
            }

            if(node.deep >= 0 && node.map.size() > 1) {
                colIndex--;
            }

            if(lastRow > firstRow || lastCol > firstCol) {

                // 合并父节点单元格
                if(lastCol > firstCol && firstRow > 0) {
                    sheet.addMergedRegion(new CellRangeAddress(firstRow+rowIndex-1, firstRow+rowIndex-1, firstCol, lastCol));
                }

                // 合并当前节点单元格
                sheet.addMergedRegion(new CellRangeAddress(firstRow+rowIndex, lastRow+rowIndex, firstCol, lastCol));
            }

            if(node.map.size() == 0 && node.getColumn()!=null) {
                writeColComment(cell, draw,  node.getColumn().comment(),firstRow+rowIndex, lastRow+rowIndex, firstCol, lastCol);
            }

        }
    }

    private void writeColOptions(Sheet sheet, ExcelColumn column,
                                 int firstRow, int lastRow, int firstCell, int lastCell) throws Exception {

        if (null != column.options()) {
            Options options;
            Class clazz = column.options();
            if(clazz.isEnum()) {
                options = EnumTool.getFirstValue(clazz); // 枚举不能new产生，所以这里折中通过遍历获取第一个枚举对象
            } else {
                options = column.options().newInstance();
            }
            String[] datasource =  options.get();
            if (null != datasource && datasource.length > 0) {
                if (datasource.length > 100) {
                    throw new WriterException("Options item too much.");
                }

                DataValidationHelper validationHelper = sheet.getDataValidationHelper();
                DataValidationConstraint explicitListConstraint = validationHelper
                        .createExplicitListConstraint(datasource);
                CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCell,
                        lastCell);
                DataValidation validation = validationHelper
                        .createValidation(explicitListConstraint, regions);
                validation.setSuppressDropDownArrow(true);
                validation.createErrorBox("提示", "请从下拉列表选取");
                validation.setShowErrorBox(true);
                sheet.addValidationData(validation);
            }
        }
    }

    private void writeColComment(Cell cell, Drawing draw, String comment,
                                 int firstRow, int lastRow, int firstCell, int lastCell) {

        if(S.isNotEmpty(comment) && cell!=null) {

            if(excelType == ExcelType.XLSX) {
                Comment cellComment = draw.createCellComment(//
                        new XSSFClientAnchor(0, 0, 0, 0, firstCell, firstRow, lastCell, lastRow));
                XSSFRichTextString xssfRichTextString = new XSSFRichTextString(
                        comment);
                Font commentFormatter = workbook.createFont();
                xssfRichTextString.applyFont(commentFormatter);
                cellComment.setString(xssfRichTextString);
                cell.setCellComment(cellComment);
            } else {
                ClientAnchor anchor = factory.createClientAnchor();
                Comment comment0 = draw.createCellComment(anchor);
                RichTextString str0 = factory.createRichTextString(comment);
                comment0.setString(str0);
                cell.setCellComment(comment0);
            }

        }

    }

    private void writeRow(Sheet sheet, Object instance, CellStyle columnStyle) throws Exception {
        Row row = sheet.createRow(rowNum++);
        for (Integer index : fieldIndexes.keySet()) {

            Field field = fieldIndexes.get(index);
            if (null == field) {
                continue;
            }

            Object value = field.get(instance);
            if (value == null) {
                continue;
            }

            Cell cell = row.createCell(index);
            if (null != columnStyle) {
                cell.setCellStyle(columnStyle);
            }

            String fieldValue = computeColumnContent(value, field);
            cell.setCellValue(fieldValue);

            if(columns.size() > index) {
                ExcelColumn column = columns.get(index);
                writeColOptions(sheet, column, rowNum-1, rowNum-1, index, index);
            }

        }
    }

    String computeColumnContent(Object value, Field field) throws Exception {
        if (field.getType().equals(String.class)) {
            return value.toString();
        }
        ExcelColumn column = field.getAnnotation(ExcelColumn.class);
        if (!NullConverter.class.equals(column.converter())) {
            Converter convert = column.converter().newInstance();
            ConverterCache.addConvert(convert);
            return convert.toString(value);
        } else {
            if (S.isNotEmpty(column.dateFormat())) {
                String content = "";
                if (Date.class.equals(field.getType())) {
                    content = new DateConverter(column.dateFormat()).toString((Date) value);
                } else if (LocalDate.class.equals(field.getType())) {
                    content = new LocalDateConverter(column.dateFormat()).toString((LocalDate) value);
                }
                if (LocalDateTime.class.equals(field.getType())) {
                    content = new LocalDateTimeConverter(column.dateFormat()).toString((LocalDateTime) value);
                }
                return content;
            } else {
                Converter converter = ConverterCache.computeConvert(field);
                if (null != converter) {
                    return converter.toString(value);
                } else {
                    return value.toString();
                }
            }
        }
    }

}
