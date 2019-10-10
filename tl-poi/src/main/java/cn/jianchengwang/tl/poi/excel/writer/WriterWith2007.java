
package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.OutputStream;

/**
 * Excel Writer by 2007
 *
 */
public class WriterWith2007 extends ExcelWriter {

    public WriterWith2007(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public void writeSheet(Writer writer) throws WriterException {
        this.workbook = new SXSSFWorkbook(writer.bufferSize());
        super.writeSheet(writer);
    }

}
