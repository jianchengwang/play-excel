package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.poi.excel.Constant;
import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.config.Table;
import cn.jianchengwang.tl.poi.excel.config.extmsg.ExtMsg;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;
import cn.jianchengwang.tl.poi.excel.config.style.StyleConfig;
import cn.jianchengwang.tl.poi.excel.converter.*;
import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Comparator.comparingInt;

@Slf4j
public abstract class ExcelWriter {

    private int                 rowNum;
    private Map<Integer, Field> fieldIndexes;
    private List<ExcelColumn> columns;

    Workbook workbook;
    OutputStream outputStream;

    ExcelWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    ExcelWriter() {
    }

    void writeSheet(Writer writer) throws WriterException {

        Collection<Table<?>> tables = writer.tables();
        if(!writer.haveMultipleSheet()) {
            tables = new ArrayList<>();
            tables.add(
                    Table.create(Object.class, 0, writer.sheetName())
                            .startRow(writer.startRow())
                            .headLineRow(writer.startRow())
                            .headTitle(writer.headerTitle())
                            .data(writer.rows())
                            .styleConfig(writer.styleConfig())
            );
        }

        for(Table table : tables) {
            writeSheet0(writer, table);
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
    void writeSheet0(Writer writer, Table table) throws WriterException {

        // create sheet
        Sheet sheet = workbook.createSheet(table.sheetName());

        // setting styles
        CellStyle headerStyle = Constant.defaultHeaderStyle(workbook);
        CellStyle columnStyle = Constant.defaultColumnStyle(workbook);
        CellStyle titleStyle  = Constant.defaultTitleStyle(workbook);

        StyleConfig styleConfig = table.styleConfig();
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

        if (writer.isRaw()) {
            writer.sheetConsumer().accept(sheet);
        } else {
            // compute the Filed to be written
            Collection<?> rows   = table.data();
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
            // write title
            String title = table.headTitle();
            if (table.haveHeadTitle()) {
                Integer maxColIndex = columns.stream()
                        .map(ExcelColumn::index)
                        .max(comparingInt(Integer::intValue))
                        .get();

                this.writeHeader(titleStyle, sheet, title, maxColIndex);

                colRowIndex = 1;
            }

            // write extMsg
            if(table.extMsgConfig().haveExtMsg()) {
                this.writeExtMsgList(sheet, table);

                colRowIndex += (table.extMsgConfig().extMsgRow() + 1);
            }

            this.rowNum = table.startRow();
            if (this.rowNum == 0) {
                this.rowNum = colRowIndex + table.headLineRow();
            }

            try {
                // write column header
                this.writeColumnNames(sheet, table, colRowIndex, headerStyle);

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

    private void writeExtMsgList(Sheet sheet, Table table) {

        int startRow = 0;
        if(table.haveHeadTitle()) startRow = 1;

        int t = table.extMsgConfig().extMsgTotal();
        int r = table.extMsgConfig().extMsgRow();
        int c = table.extMsgConfig().extMsgCol();
        int s = table.extMsgConfig().extMsgColSpan();
        List<ExtMsg> extMsgList = table.extMsgList();

        int extMsgListIndex = 0;
        for(int ri=0; ri<table.extMsgConfig().extMsgRow(); ri++) {

            Row row = sheet.createRow(ri + startRow);

            for(int ci=0; ci<(2 + s)*c-1; ci++) {

                if(extMsgListIndex == extMsgList.size()) return;

                ExtMsg extMsg = extMsgList.get(extMsgListIndex);

                if(ci % (2 + s) == 0) {
                    Cell cell = row.createCell(ci);
                    cell.setCellValue(extMsg.getTitle());
                } else if(ci % (2 + s) == 1){
                    Cell cell = row.createCell(ci);
                    cell.setCellValue(extMsg.getMsg());

                    ci += s;
                    extMsgListIndex++;
                }
            }

        }

    }

    private void writeColumnNames(Sheet sheet, Table table, int rowIndex, CellStyle headerStyle) {

        if(table.headLineRow() == 1) {
            Row rowHead = sheet.createRow(rowIndex);
            for (ExcelColumn column : columns) {
                Cell cell = rowHead.createCell(column.index());
                if (null != headerStyle) {
                    cell.setCellStyle(headerStyle);
                }
                cell.setCellValue(column.title()[0]);
                if (column.width() > 0) {
                    sheet.setColumnWidth(column.index(), column.width());
                } else {
                    sheet.setColumnWidth(column.index(), Constant.DEFAULT_COLUMN_WIDTH);
                }
            }
        } else {
            // 迭代Map集合，并重构一套“根目录”
            HeaderColumnNode root = new HeaderColumnNode();
            List<HeaderColumnNode> headerColumnNodes = root.build(columns);

            final int rootDeepLength = root.getDeep();

            // 创建多个行，并用数组存储
            Row[] rows = new Row[rootDeepLength];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = sheet.createRow(rowIndex + i);
            }


            //4.2 遍历所有结点
            int columnIndex = 0;
            for(String text: root.map.keySet()) {
                columnIndex += writeColumnName(sheet, root.map.get(text), rows, rootDeepLength, rowIndex, columnIndex);
            }

//            int columnIndex = 0;
//            for (HeaderColumnNode node : headerColumnNodes) {
//                //获取该节点的深度
//                int deep = node.getDeep();
//                System.out.println(deep);
//                //从下往上取行，向右创建
//                int topRowIndex = rows.length - deep - 1;//计算这个结点的控制范围上限
//                for (int i = 0; i <= deep; i++) {
//                    rows[i].createCell(columnIndex);
//                }
//                rows[topRowIndex].getCell(columnIndex).setCellValue(node.getText());
//
//                if(deep == 0) {
//                    columnIndex++;
//                }
//            }

//            int[] columnIndexArr = new int[rootDeepLength];
//
//            // 遍历所有结点
//            for (HeaderColumnNode node : headerColumnNodes) {
//                //获取该节点的深度
//                int deep = node.getDeep();
//                //深度为0，这是普通一级结点
//                if (deep == 0) {
//                    //从下往上取行，向右创建
//                    int topRowIndex = node.getDeep();//获取这个结点的控制范围上限
//                    int bottomRowIndex = rows.length - deep - 1;//计算这个结点的控制范围下限
//                    for (int i = rows.length - 1; i >= 0; i--) {
//                        rows[i].createCell(columnIndexArr[i]);
//                    }
//                    rows[topRowIndex].getCell(columnIndexArr[topRowIndex]).setCellValue(node.getText());
//                    //一列多行，但如果只有一行，就没有必要合并了
//                    if (topRowIndex != bottomRowIndex) {
////                        sheet.addMergedRegion(new CellRangeAddress(rowIndex + topRowIndex, rowIndex + bottomRowIndex, columnIndexArr[topRowIndex], columnIndexArr[topRowIndex]));
//                    }
//                    //涉及到的列的下标数组统一往后推一格
//                    for (int i = topRowIndex; i <= bottomRowIndex; i++) {
//                        columnIndexArr[i] += 1;
//                    }
//                    //最后一行一定全是叶子结点，要控制列宽
////                    sheet.setColumnWidth(columnIndexArr[columnIndexArr.length - 1], node.getWidth() * 2 * 256);
//                }else {
//                    //深度不为0，复合结点，需要复杂构建
//                    //从下往上取行，向右创建
//                    int topRowIndex = node.getDeep();//获取这个结点的控制范围上限
//                    int bottomRowIndex = rows.length - deep - 1;//计算这个结点的控制范围下限
//                    int childrenCount = node.getChildrenCount();
//                    //并行创建，能控制到的每一行都要创建足够的容量使得下面的叶子结点能放得下
//                    for (int i = bottomRowIndex; i >= topRowIndex; i--) {
//                        for (int j = 0; j < childrenCount; j++) {
//                            rows[i].createCell(columnIndexArr[i] + j);
//                        }
//                        columnIndexArr[i] += childrenCount;
//                    }
//                    //填充值，合并单元格（不需要判定是否为一个单元格）
//                    rows[bottomRowIndex].getCell(columnIndexArr[bottomRowIndex] - childrenCount).setCellValue(node.getText());
//                    sheet.addMergedRegion(new CellRangeAddress(rowIndex + topRowIndex, rowIndex + bottomRowIndex, columnIndexArr[topRowIndex] - childrenCount, columnIndexArr[topRowIndex] - 1));
//                }
//            }
//            rowIndex += rows.length;

            //表头的数据应该是很多单元格的合并、居中
            //四个参数：开始行，结束行，开始列，结束列
            //因为上面加了1，这里还要抵消掉
//            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnIndexArr[columnIndexArr.length - 1] - 1));
        }
    }

    private int writeColumnName(Sheet sheet, HeaderColumnNode node, Row[] rows, int rootDeep, int rowIndex, int columnIndex) {

        if (node.map != null && node.map.size() > 0) {

            for(int i=node.deep; i<rootDeep-1; i++) {
                for(int j=columnIndex; j<columnIndex + node.map.size(); j++) {
                    rows[i].createCell(j);
                }
            }
            rows[node.deep].getCell(columnIndex).setCellValue(node.text);
            System.out.println(node.deep + ":" + columnIndex + ":" + node.text);
            if(node.deep > 0 || node.map.size() - 1 > 0) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex , rowIndex + node.deep, columnIndex, columnIndex + node.map.size() - 1));
            }

            for(String text: node.map.keySet()) {

                if(node.map.get(text) != null && node.map.size()>0) {
                    columnIndex += writeColumnName(sheet, node.map.get(text), rows, rootDeep, rowIndex, columnIndex);
                }
            }

            return node.map.size();

        } else {

            for(int i=node.deep; i<rootDeep; i++) {
                rows[i].createCell(columnIndex);
            }
            rows[node.deep].getCell(columnIndex).setCellValue(node.text);

            if(rootDeep - node.deep - 1 > 0) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex , rowIndex + node.deep + 1, columnIndex, columnIndex));
            }
            System.out.println(node.deep + ":" + columnIndex + ":" + node.text);

            return 1;
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
            if (StrKit.isNotEmpty(column.format())) {
                String content = "";
                if (Date.class.equals(field.getType())) {
                    content = new DateConverter(column.format()).toString((Date) value);
                } else if (LocalDate.class.equals(field.getType())) {
                    content = new LocalDateConverter(column.format()).toString((LocalDate) value);
                }
                if (LocalDateTime.class.equals(field.getType())) {
                    content = new LocalDateTimeConverter(column.format()).toString((LocalDateTime) value);
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

    @Data
    private class HeaderColumnNode {
        private String text; // 文本信息
        private Integer width; //这个单元格应该在Excel中占有的宽度
        private Integer deep; // 单元格深度
        private Map<String, HeaderColumnNode> map; // 子节点 map 集合

        public HeaderColumnNode(String text, Integer width, Integer deep) {
            this.text = text;
            this.width = width;
            this.deep = deep;

            this.map = new LinkedHashMap<>();
        }

        public HeaderColumnNode(String text, Integer width) {
            this.text = text;
            this.width = width;

            this.map = new LinkedHashMap<>();
        }
        public HeaderColumnNode(String text) {
            this.text = text;
            this.map = new LinkedHashMap<>();
        }
        public HeaderColumnNode() {
            this.map = new LinkedHashMap<>();
        }

         // 添加节点
       void add(String[] text, Integer width) {

           Map<String, HeaderColumnNode> rootMap = map;

           HeaderColumnNode node =  null;
            //读到叶子结点的前一个结点处
           for (int i = 0; i < text.length-1; i++) {
               //逐层目录读取，如果没有get到，就创建一个新的目录
               node = rootMap.get(text[i]);
               if (node == null) {
                   node = new HeaderColumnNode(text[i]);
                   rootMap.put(text[i], node);
               }
               //新目录的大小要同步上
               if(node.getWidth() == null) node.setWidth(Constant.DEFAULT_COLUMN_WIDTH);
               node.setWidth(node.getWidth() + width);
               rootMap = node.getMap();
           }
           //此时的rootMap就是叶子结点所在的目录
           rootMap.put(text[text.length - 1], new HeaderColumnNode(text[text.length - 1], width, text.length - 1));

           //还要给这个文件的父文件夹设置deep
           if (node != null) {
               node.setDeep(text.length - 2);
           }
        }

        // 得到节点集合
        List<HeaderColumnNode> parse() {
            List<HeaderColumnNode> list = new ArrayList<>();
            for (Map.Entry<String, HeaderColumnNode> entry : map.entrySet()) {
                //先把自己保存进去
                list.add(entry.getValue());
                //如果该节点的map不是空集合，证明这是一个“文件夹”（根节点）
                //需要把自己add进去的同时，把它的孩子也全部add进去
                if (entry.getValue().getMap() != null && entry.getValue().getMap().size() > 0) {
                    list.addAll(entry.getValue().parse());
                }
            }
            return list;
        }

        // 计算深度
        int getDeep() {
            if (map.isEmpty()) {
                return 0;
            }
            List<Integer> list = new ArrayList<>();
            for (Map.Entry<String, HeaderColumnNode> entry : map.entrySet()) {
                list.add(entry.getValue().getDeep());
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
            for (Map.Entry<String, HeaderColumnNode> entry : map.entrySet()) {
                count += entry.getValue().getChildrenCount();
            }
            return count;
        }

        List<HeaderColumnNode> build(List<ExcelColumn> columns) {

            for(ExcelColumn column: columns) {
                add(column.title(), column.width()>0? column.width(): Constant.DEFAULT_COLUMN_WIDTH);
            }

            return parse();

        }
    }

}
