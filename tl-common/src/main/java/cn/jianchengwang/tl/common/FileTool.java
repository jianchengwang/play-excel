package cn.jianchengwang.tl.common;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;

/**
 * Created by wjc on 2019/9/2
 **/
@UtilityClass
public class FileTool {

    private static final int BUFFER_SIZE = 8192;

    public final static HashMap<String, String> MIME_MAP = new HashMap();
    static {
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("wbmp", "image/vnd.wap.wbmp");

        MIME_MAP.put("doc", "application/msword");
        MIME_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_MAP.put("xls", "application/vnd.ms-excel");
        MIME_MAP.put("xlsx", "application/vnd.ms-works");
        MIME_MAP.put("wps", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("ppt", "application/vnd.ms-powerpoint");
        MIME_MAP.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");

        MIME_MAP.put("txt", "text/plain");
        MIME_MAP.put("java", "text/plain");
        MIME_MAP.put("js", "text/plain");
        MIME_MAP.put("cs", "text/plain");
        MIME_MAP.put("html", "text/html");
    }


    public static long copy(InputStream source, OutputStream sink)
            throws IOException {
        @Cleanup InputStream ins = source;
        @Cleanup OutputStream os = sink;
        long totalBytes = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = ins.read(buf)) > 0) {
            os.write(buf, 0, bytesRead);
            totalBytes += bytesRead;
        }
        return totalBytes;
    }

    /**
     * 从InputStream获取File
     *
     * @param inputStream
     * @param file
     * @throws IOException
     */
    public static void inputStreamToFile(InputStream inputStream, File file) throws IOException {
        @Cleanup InputStream ins = inputStream;
        @Cleanup OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = ins.read(buffer, 0, BUFFER_SIZE)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        @Cleanup InputStream ins = inputStream;
        @Cleanup ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[BUFFER_SIZE];
        int i;
        while ((i = ins.read(bytes)) != -1) {
            baos.write(bytes, 0, i);
        }
        return baos.toByteArray();
    }
}
