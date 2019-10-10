
package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
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

public class ReaderWith2007 implements ExcelReader {

    public ReaderWith2007(Workbook workbook) {
        // ignore
    }

    public <T> void readExcel(Reader reader) throws ReaderException {
        Class<T> clazz = reader.gridSheet().clazz();
        try {
            // The package open is instantaneous, as it should be.
            try (OPCPackage p = getPackage(reader)) {

                SheetToCSV<T> sheetToCSV = new SheetToCSV<T>(p, reader);

                this.process(reader, sheetToCSV);

                reader.setGridSheetStream(sheetToCSV.getGridSheetStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReaderException(e);
        }
    }

    private OPCPackage getPackage(Reader reader) throws Exception {
        return OPCPackage.open(reader.from());
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

        boolean isGetSingleSheet = S.isNotEmpty(reader.getGridSheet().sheetName()) || reader.getGridSheet().sheetIndex()>-1;
        while (iter.hasNext()) {
            try (InputStream stream = iter.next()) {

                if(isGetSingleSheet) {
                    boolean bySheetName = S.isNotEmpty(reader.gridSheet().sheetName());
                    String sheetName = iter.getSheetName();

                    sheetToCSV.gridSheet(GridSheet.build()
                            .clazz(reader.gridSheet().clazz())
                            .sheetIndex(index)
                            .sheetName(sheetName)
                            .extraInfo(reader.gridSheet().extraInfo()));
                    if (bySheetName && reader.gridSheet().sheetName().equals(sheetName)) {
                        processSheet(styles, strings, sheetToCSV, stream);
                        break;
                    }
                    if (!bySheetName && reader.gridSheet().sheetIndex() == index) {
                        processSheet(styles, strings, sheetToCSV, stream);
                        break;
                    }
                }

                sheetToCSV.gridSheet(GridSheet.build()
                        .clazz(reader.gridSheet().clazz())
                        .sheetIndex(index)
                        .sheetName(iter.getSheetName())
                        .extraInfo(reader.gridSheet().extraInfo()));
                processSheet(styles, strings, sheetToCSV, stream);

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
