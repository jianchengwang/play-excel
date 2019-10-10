package cn.jianchengwang.tl.poi.excel.config;

import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.excel.Const;
import cn.jianchengwang.tl.poi.excel.annotation.ExcelColumn;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.ExtraInfo;
import cn.jianchengwang.tl.poi.excel.config.style.StyleConfig;
import lombok.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wjc on 2019/9/3
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridSheet<T> {
    private Integer sheetIndex = -1; // sheet 索引
    private String sheetName = Const.DEFAULT_SHEET_NAME; // sheet名称

    // 以下字段只对grid有效
    private @Getter
    @Setter
    ExtraInfo extraInfo; // 附加信息
    private @Getter
    @Setter
    Class<T> clazz; // 映射实体类型
    private @Getter
    @Setter
    List<String> headers; // 头部字段
    private @Getter
    @Setter
    List<String> props; // 映射实体字段
    private @Getter
    @Setter
    long totalRow; // 列表总数据
    private @Getter
    @Setter
    List<T> data; // 列表数据
    // 读取grid用到
    private  @Getter
    @Setter
    Integer startRow = 0;
    private @Getter
    @Setter
    Integer headLineRow = 1;

    private @Getter
    @Setter
    StyleConfig styleConfig; // 样式配置文件

    // 便于子类继承
    public GridSheet(GridSheet gridSheet) {
        this(gridSheet.sheetIndex,
                gridSheet.getSheetName(),
                gridSheet.getExtraInfo(),
                gridSheet.getClazz(),
                gridSheet.getHeaders(),
                gridSheet.getProps(),
                gridSheet.getTotalRow(),
                gridSheet.getData(),
                gridSheet.getStartRow(),
                gridSheet.getHeadLineRow(),
                gridSheet.getStyleConfig());
        this.calTotalRow();
    }

    public GridSheet(List<T> items) {
        this.data = items;
        this.calTotalRow();
    }

    public GridSheet(Class<T> clazz, List<T> items) {
        this(items);

        List<GridColumn> gridList = new ArrayList<>();
        this.clazz = clazz;
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (null != excelColumn) {
                field.setAccessible(true);

                GridColumn gridColumn = GridColumn.builder()
                        .header(excelColumn.header())
                        .prop(S.isNotEmpty(excelColumn.prop())?excelColumn.prop():field.getName())
                        .index(excelColumn.index()).build();
                gridList.add(gridColumn);
            }
        });

        if(!gridList.isEmpty()) {
            if(this.headers == null) this.headers = new ArrayList<>();
            if(this.props == null) this.props = new ArrayList<>();
            gridList.sort((a, b) -> a.getIndex().compareTo(b.getIndex()));
            gridList.forEach(grid -> {
                this.headers.add(grid.getHeader());
                this.props.add(grid.getProp());
            });
        }

        this.calTotalRow();
    }

    // 链式builder方便赋值，lombok生成静态builder不支持泛型实现
    public static GridSheet build() {
        return new GridSheet();
    }

    public GridSheet clazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }
    public Class<T> clazz() {
        return this.clazz;
    }

    public GridSheet sheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }
    public Integer sheetIndex() {
        return this.sheetIndex;
    }

    public GridSheet sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    public String sheetName() {
        return this.sheetName;
    }

    public GridSheet extraInfo(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
        return this;
    }
    public ExtraInfo extraInfo() {
        return this.extraInfo;
    }

    public GridSheet headers(List<String> headers) {
        this.headers = headers;
        return this;
    }
    public List<String> headers() {
        return this.headers;
    }

    public GridSheet props(List<String> props) {
        this.props = props;
        return this;
    }
    public List<String> props() {
        return this.props;
    }

    public Long totalRow() {
        return totalRow;
    }
    public void calTotalRow() {
        this.totalRow = data.size();
    }

    public GridSheet data(List<T> data) {
        this.data = data;
        return this;
    }
    public List<T> data() {
        return this.data;
    }

    public Integer startRow() {
        return this.startRow;
    }
    public GridSheet startRow(Integer startRow) {
        this.startRow = startRow;
        return this;
    }
    public Integer headLineRow() {
        return this.headLineRow;
    }
    public GridSheet headLineRow(Integer headLineRow) {
        this.headLineRow = headLineRow;
        return this;
    }

    public StyleConfig styleConfig() {
        return this.styleConfig;
    }
    public GridSheet styleConfig(StyleConfig styleConfig) {
        this.styleConfig = styleConfig;
        return this;
    }
}
