package cn.jianchengwang.tl.poi.excel.kit;

import cn.jianchengwang.tl.common.FileTool;
import cn.jianchengwang.tl.poi.excel.enums.ExcelType;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wjc on 2019/9/5
 **/
@Slf4j
@UtilityClass
public class ExcelKit {

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Workbook create(File file) throws ReaderException {
        try {
            return WorkbookFactory.create(file);
        } catch (IOException e) {
            throw new ReaderException(e);
        }
    }

    public static Workbook create(InputStream inputStream) throws ReaderException {
        try {
            return WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            throw new ReaderException(e);
        }
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf + 1);
    }

    public static boolean isXLSX(File file) {
        if (null == file || !file.exists()) {
            return false;
        }
        String ext = getFileExtension(file.getName());
        return ext.toUpperCase().equals("XLSX");
    }

    public static boolean isXLS(File file) {
        if (null == file || !file.exists()) {
            return false;
        }
        String ext = getFileExtension(file.getName());
        return ext.toUpperCase().equals("XLS");
    }

    public static boolean isCSV(File file) {
        if (null == file || !file.exists()) {
            return false;
        }
        String ext = getFileExtension(file.getName());
        return ext.toUpperCase().equals("CSV");
    }

    public static byte[] streamAsBytes(InputStream inputStream) throws IOException {
        if (null == inputStream) {
            return null;
        }
        return FileTool.toByteArray(inputStream);
    }

    public static boolean isXLSX(InputStream inputStream) {
//        try {
//            new XSSFWorkbook(inputStream);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;

        return ExcelType.XLSX == ExcelType.valueOf(inputStream);
    }

    public static boolean isXLS(InputStream inputStream) {
//        try {
//            new HSSFWorkbook(inputStream);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;

        return ExcelType.XLS == ExcelType.valueOf(inputStream);
    }
}
