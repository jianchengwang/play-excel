
package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import cn.jianchengwang.tl.poi.excel.kit.StrKit;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Used to wrap the HttpServletResponse and download file name
 *
 */
@UtilityClass
public class ResponseWrapper {

    public static OutputStream create(HttpServletResponse servletResponse, String fileName) throws WriterException {
        try {
            if (null == servletResponse) {
                throw new WriterException("response instance not null");
            }
            if (StrKit.isEmpty(fileName)) {
                throw new WriterException("response file name not empty");
            }
            servletResponse.setContentType("application/x-xls");
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            servletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            return servletResponse.getOutputStream();
        } catch (Exception e) {
            throw new WriterException(e);
        }
    }

}