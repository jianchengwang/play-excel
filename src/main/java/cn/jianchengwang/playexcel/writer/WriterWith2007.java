
package cn.jianchengwang.playexcel.writer;

import cn.jianchengwang.playexcel.Writer;
import cn.jianchengwang.playexcel.exception.WriterException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
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
        if (writer.template() != null) {
            try {
                this.workbook = WorkbookFactory.create(writer.template());
                super.writeSheet(writer);
            } catch (IOException e) {
                throw new WriterException(e);
            }
        } else {
            this.workbook = new SXSSFWorkbook(writer.bufferSize());
            super.writeSheet(writer);
        }
    }

}
