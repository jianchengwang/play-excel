package cn.jianchengwang.playexcel;

import cn.jianchengwang.playexcel.config.Table;
import cn.jianchengwang.playexcel.enums.ExcelType;
import cn.jianchengwang.playexcel.exception.WriterException;
import cn.jianchengwang.playexcel.kit.StrKit;
import cn.jianchengwang.playexcel.config.style.StyleConfig;
import cn.jianchengwang.playexcel.writer.WriterWith2003;
import cn.jianchengwang.playexcel.writer.WriterWith2007;
import cn.jianchengwang.playexcel.writer.WriterWithCSV;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class Writer {

    /**
     * The name of the Sheet to be written to Excel. The default is Sheet0.
     */
    private String sheetName = Constant.DEFAULT_SHEET_NAME;

    /**
     * Store the row to be written
     */
    private Collection<?> rows;

    /**
     * Write from the first few lines,
     * the default is automatic calculation, calculated by Excel title and column,
     * may be 1 or 2
     */
    private int startRow;

    /**
     * Buffer when writing a document in xlsx format
     */
    private int bufferSize = 100;

    private boolean withRaw;

    /**
     * Type of excel written, select XLSX, XLS, CSV
     */
    private ExcelType excelType;

    /**
     * Write the title of Excel, optional
     */
    private String headerTitle;

    /**
     * Specify the path to the template by writing data according to the specified template
     */
    private File template;

    private StyleConfig styleConfig;

    private Consumer<Sheet> sheetConsumer;

    private Charset charset = StandardCharsets.UTF_8;

    // 多sheet配置
    private boolean haveMultipleSheet;
    private Collection<Table<?>> tables;

    public static Writer create() {
        return new Writer(ExcelType.XLSX);
    }

    public static Writer create(ExcelType excelType) {
        return new Writer(excelType);
    }

    public Writer(ExcelType excelType) {
        this.excelType = excelType;
    }

    /**
     * Set the data to be written, receive a collection
     *
     * @param rows row data
     * @return Writer
     */
    public Writer withRows(Collection<?> rows) {
        this.rows = rows;
        return this;
    }

    /**
     * Configure the name of the sheet to be written. The default is Sheet0.
     *
     * @param sheetName sheet name
     * @return Writer
     */
    public Writer sheet(String sheetName) {
        if (StrKit.isEmpty(sheetName)) {
            throw new IllegalArgumentException("sheet cannot be empty");
        }
        this.sheetName = sheetName;
        return this;
    }

    /**
     * Set the data to be written from the first few lines.
     * By default, the value is calculated. It is recommended not to modify it.
     *
     * @param startRow start row index
     * @return Writer
     */
    public Writer start(int startRow) {
        if (startRow < 0) {
            throw new IllegalArgumentException("start cannot be less than 0");
        }
        this.startRow = startRow;
        return this;
    }

    /**
     * Set the title of the Excel table, do not write the title without setting
     *
     * @param title excel title
     * @return Writer
     */
    public Writer headerTitle(String title) {
        this.headerTitle = title;
        return this;
    }


    /**
     * Specify to write an Excel table from a template file
     *
     * @param templatePath template file path
     * @return Writer
     */
    public Writer withTemplate(String templatePath) {
        return this.withTemplate(new File(templatePath));
    }

    /**
     * Specify to write an Excel table from a template file
     *
     * @param template template file instance
     * @return Writer
     */
    public Writer withTemplate(File template) {
        if (null == template || !template.exists()) {
            throw new IllegalArgumentException("template file not exist");
        }
        this.template = template;
        return this;
    }

    /**
     * This setting is only valid for xlsx format Excel
     * <p>
     * The default buffer is 100, which can be adjusted according to the number of write lines.
     * <p>
     * If you are not sure, please do not set
     *
     * @param bufferSize
     * @return
     */
    public Writer bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public Writer createRow(Consumer<Sheet> sheetConsumer) {
        this.sheetConsumer = sheetConsumer;
        return this;
    }

    public Writer withRaw() {
        this.withRaw = true;
        return this;
    }

    public Writer charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Write an Excel document to a file
     *
     * @param file excel file
     * @throws WriterException
     */
    public void to(File file) throws WriterException {
        try {
            this.to(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new WriterException(e);
        }
    }

    /**
     * Write an Excel document to the output stream
     *
     * @param outputStream outputStream
     * @throws WriterException
     */
    public void to(OutputStream outputStream) throws WriterException {
//        if (!withRaw && (null == rows || rows.isEmpty())) {
//            throw new WriterException("write rows cannot be empty, please check it");
//        }
        if (excelType == ExcelType.XLSX) {
            new WriterWith2007(outputStream).writeSheet(this);
        }
        if (excelType == ExcelType.XLS) {
            new WriterWith2003(outputStream).writeSheet(this);
        }
        if (excelType == ExcelType.CSV) {
            new WriterWithCSV(outputStream).writeSheet(this);
        }
    }

    public int startRow() {
        return this.startRow;
    }

    public String sheetName() {
        return this.sheetName;
    }

    public File template() {
        return this.template;
    }

    public String headerTitle() {
        return this.headerTitle;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public Collection<?> rows() {
        return rows;
    }

    public Consumer<Sheet> sheetConsumer() {
        return sheetConsumer;
    }

    public boolean isRaw() {
        return withRaw;
    }

    public Charset charset() {
        return this.charset;
    }

    public Writer withTable(Table<?> table) {
        if(this.tables == null) this.tables = new ArrayList<>();
        this.tables.add(table); this.haveMultipleSheet = true;
        return this;
    }

    public Writer withTables(Collection<Table<?>> tables) {
        this.tables = tables; this.haveMultipleSheet = true;
        return this;
    }

    public Collection<Table<?>> tables() {
        return this.tables;
    }

    public boolean haveMultipleSheet() {
        return this.haveMultipleSheet;
    }

    public Writer styleConfig(StyleConfig styleConfig) {
        this.styleConfig = styleConfig;
        return this;
    }

    public StyleConfig styleConfig() {
        return this.styleConfig;
    }

}

