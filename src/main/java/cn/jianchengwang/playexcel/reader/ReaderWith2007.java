
package cn.jianchengwang.playexcel.reader;

import cn.jianchengwang.playexcel.Reader;
import cn.jianchengwang.playexcel.exception.ReaderException;
import cn.jianchengwang.playexcel.kit.StrKit;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class ReaderWith2007 implements ExcelReader {

    public ReaderWith2007(Workbook workbook) {
        // ignore
    }

    public <T> Stream<T> readExcel(Reader reader) throws ReaderException {
        Class<T> type = reader.sheet().modelType();
        try {
            // The package open is instantaneous, as it should be.
            try (OPCPackage p = getPackage(reader)) {

                SheetToCSV<T> sheetToCSV = new SheetToCSV<>(p, reader.sheet().headLineRow(), type);

                this.process(reader, sheetToCSV);

                Stream.Builder<T> stream = sheetToCSV.getRowsStream();
                return stream.build();
            }
        } catch (Exception e) {
            throw new ReaderException(e);
        }
    }

    private OPCPackage getPackage(Reader reader) throws Exception {
        if (reader.fromFile() != null) {
            return OPCPackage.open(reader.fromFile(), PackageAccess.READ);
        } else {
            return OPCPackage.open(reader.fromStream());
        }
    }

    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws IOException  If reading the data from the package fails.
     * @throws SAXException if parsing the XML data fails.
     */
    public void process(Reader reader, SheetToCSV sheetToCSV) throws IOException, OpenXML4JException, SAXException {
        ReadOnlySharedStringsTable strings    = new ReadOnlySharedStringsTable(sheetToCSV.getOpcPackage());
        XSSFReader xssfReader = new XSSFReader(sheetToCSV.getOpcPackage());
        StylesTable styles     = xssfReader.getStylesTable();
        XSSFReader.SheetIterator   iter       = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int                        index      = 0;

        boolean bySheetName = StrKit.isNotEmpty(reader.sheet().sheetName());

        while (iter.hasNext()) {
            try (InputStream stream = iter.next()) {
                String sheetName = iter.getSheetName();
                if (bySheetName && reader.sheet().sheetName().equals(sheetName)) {
                    processSheet(styles, strings, sheetToCSV, stream);
                    break;
                }
                if (!bySheetName && reader.sheet().sheetIndex() == index) {
                    processSheet(styles, strings, sheetToCSV, stream);
                    break;
                }
            }
            ++index;
        }
    }

    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *
     * @param styles           The table of styles that may be referenced by cells in the sheet
     * @param strings          The table of strings that may be referenced by cells in the sheet
     * @param sheetInputStream The stream to read the sheet-data from.
     * @throws IOException An IO exception from the parser,
     *                             possibly from a byte stream or character stream
     *                             supplied by the application.
     * @throws SAXException        if parsing the XML data fails.
     */
    public void processSheet(
            Styles styles,
            SharedStrings strings,
            XSSFSheetXMLHandler.SheetContentsHandler sheetHandler,
            InputStream sheetInputStream) throws IOException, SAXException {

        DataFormatter formatter   = new XLSXDataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);

        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

}
