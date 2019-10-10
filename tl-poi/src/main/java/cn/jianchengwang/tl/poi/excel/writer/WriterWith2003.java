
package cn.jianchengwang.tl.poi.excel.writer;

import cn.jianchengwang.tl.poi.excel.Writer;
import cn.jianchengwang.tl.poi.excel.exception.WriterException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.OutputStream;


/**
 * Excel Writer by 2003
 *
 */
public class WriterWith2003 extends ExcelWriter {

    public WriterWith2003(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public void writeSheet(Writer writer) throws WriterException {
            this.workbook = new HSSFWorkbook();
            super.writeSheet(writer);
    }

}
