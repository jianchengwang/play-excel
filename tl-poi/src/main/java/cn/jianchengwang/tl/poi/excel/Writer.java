package cn.jianchengwang.tl.poi.excel;

import cn.jianchengwang.tl.common.E;
import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.common.ResponseWrapper;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.enums.ExcelType;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.writer.*;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Sheet;
import org.jxls.common.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 参考biezhi:excel-plus
 * https://github.com/biezhi/excel-plus
 * 增加扩展信息，多sheet，jxls模板渲染等，后面将完善options下拉列表，校验器，格式器等
 * Created by wjc on 2019/9/5
 **/
@Data
public class Writer {
    private ExcelType excelType = ExcelType.XLSX; // 模板类型

    private Context context; // 模板上下文对象
    private InputStream template; // 模板文件输入流
    private OutputStream out; // 输出流
    private String saveFileName; // 保存文件名字

    private boolean isMultiGrid; // 是否是多grid模板
    private boolean enableAutoGroupBy; // 是否自动开启group by
    private List<GridSheet> gridSheetList; // sheetBuilder配置信息
    private String[] removeSheetNames; // 需要移除sheetName

    private Consumer<Sheet> sheetConsumer;
    /**
     * Buffer when writing a document in xlsx format
     */
    private int bufferSize = 100;
    private boolean withRaw;

    public Writer(ExcelType excelType) {
        this.excelType = excelType;
    }

    public static Writer create() {
        return new Writer(ExcelType.XLSX);
    }

    public static Writer create(ExcelType excelType) {
        return new Writer(excelType);
    }

    public Writer template(InputStream in) {
        this.template = in;
        this.excelType = ExcelType.valueOf(this.template);
        this.context = new Context();
        return this;
    }

    public Writer template(String fullPath) {
        return template(new File(fullPath));
    }

    public Writer template(File templateFile) {
        try {
            return this.template(new FileInputStream(templateFile));
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public void out(File outFile) {
        try {
            saveFileName(outFile.getName());
            if(!this.saveFileName.equalsIgnoreCase(outFile.getName())) {
                outFile = new File(outFile.getParent() + File.separator + this.saveFileName);
            }
            if(!outFile.exists()) {
                outFile.createNewFile();
            }
            this.out(new FileOutputStream(outFile));
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }

    public void out(OutputStream out) throws WriterException {
        this.out = out;
        write();
    }

    public void out(HttpServletResponse servletResponse, String fileName) throws WriterException {
        saveFileName(fileName);
        this.out(ResponseWrapper.create(servletResponse, this.saveFileName));
    }

    public Writer saveFileName(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(S.isNotEmpty(suffix) && !suffix.equalsIgnoreCase(this.excelType.getValue())) {
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + this.excelType.getValue();
        }
        this.saveFileName = fileName;
        return this;
    }

    public Writer addSheetBuilderList(@NonNull List<? extends GridSheet> gridSheetList) {
        if(this.gridSheetList == null) this.gridSheetList = new ArrayList<>();
        this.gridSheetList.addAll(gridSheetList);
        this.isMultiGrid = this.isMultiGrid || this.gridSheetList.stream().anyMatch(item -> !item.getSheetName().equals(this.gridSheetList.get(0).getSheetName()));
        return this;
    }
    public Writer addSheetBuilder(@NonNull GridSheet gridSheet) {
        if(this.gridSheetList == null) this.gridSheetList = new ArrayList<>();
        this.gridSheetList.add(gridSheet);
        this.isMultiGrid = this.isMultiGrid || this.gridSheetList.stream().anyMatch(item -> !item.getSheetName().equals(this.gridSheetList.get(0).getSheetName()));
        return this;
    }
    public Writer addSheetBuilder(@NonNull Class<?> clazz, @NonNull List<?> items) {

        final Field[] groupFiled = new Field[1];
        boolean needGroupBy = this.enableAutoGroupBy && Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> {
            ExcelColumn column = field.getAnnotation(ExcelColumn.class);
            if (null != column) {
                field.setAccessible(true);
                if(column.groupBy()) {
                    groupFiled[0] = field;
                    return true;
                }
            }
            return false;
        });

        // 如果需要，则要分成多个sheet展示
        if(needGroupBy) {
            isMultiGrid = true;
            Map<String, List<Object>> mapList = items.stream().collect(Collectors.groupingBy(item -> {
                try {
                    return groupFiled[0].get(item).toString();
                } catch (IllegalAccessException e) {
                    throw E.unexpected(e);
                }
            }));

            final GridSheet[] gridSheetList = new GridSheet[1];
            mapList.forEach((k, v) -> {
                if(gridSheetList[0] == null) {
                    gridSheetList[0] = new GridSheet(clazz, v);
                    gridSheetList[0].setSheetIndex(0);
                    gridSheetList[0].setSheetName(k);
                    addSheetBuilder(gridSheetList[0]);
                } else {
                    GridSheet gridSheet = GridSheet.build()
                            .clazz(gridSheetList[0].clazz())
                            .sheetName(k)
                            .headers(gridSheetList[0].headers())
                            .props(gridSheetList[0].props())
                            .data(v);
                    addSheetBuilder(gridSheet);
                }
            });

            return this;

        } else {
            GridSheet gridSheet = new GridSheet(clazz, items);
            return addSheetBuilder(gridSheet);
        }
    }

    public Writer enableAutoGroupBy(boolean enableAutoGroupBy) {
        this.enableAutoGroupBy = enableAutoGroupBy;
        return this;
    }

    public Writer putVar(String name, Object value) {

        if(value !=null) {
            if(value instanceof Collection && !((Collection) value).isEmpty()) {
                context.putVar(name, value);
            } else {
                context.putVar(name, value);
            }
        }
        return this;
    }
    public Writer putAll(Map<String, Object> map) {
        for (String key : map.keySet()) {
            putVar(key, map.get(key));
        }
        return this;
    }
    public Writer removeVar(String name) {
        context.removeVar(name);
        return this;
    }
    public Object getVar(String name) {
        return context.getVar(name);
    }

    public Writer removeSheet(String... sheetNames){
        this.removeSheetNames = sheetNames;
        return this;
    }


    public Writer bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
    public int bufferSize() {
        return this.bufferSize;
    }

    public Consumer<Sheet> sheetConsumer() {
        return sheetConsumer;
    }
    public Writer witchRaw(boolean withRaw) {
        this.withRaw = withRaw;
        return this;
    }
    public boolean withRaw() {
        return this.withRaw;
    }

    private void write() throws WriterException {
        if(template != null) {
            new JxlsBuilder(this).build();
        } else if(excelType.equals(ExcelType.CSV)) {
            new WriterWithCSV(this.getOut()).writeSheet(this);
        }  if (excelType == ExcelType.XLSX) {
            new WriterWith2007(this.getOut()).writeSheet(this);
        }
        if (excelType == ExcelType.XLS) {
            new WriterWith2003(this.getOut()).writeSheet(this);
        }
    }
}
