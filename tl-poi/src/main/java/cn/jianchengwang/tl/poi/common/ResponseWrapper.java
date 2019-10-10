package cn.jianchengwang.tl.poi.common;

import cn.jianchengwang.tl.common.E;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by wjc on 2019/9/5
 **/
@Slf4j
@UtilityClass
public class ResponseWrapper {
    public static OutputStream create(HttpServletResponse servletResponse, String fileName) {
        try {
            servletResponse.setContentType("application/x-xls");
            fileName =  new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1" );
            servletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            return servletResponse.getOutputStream();
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
}
