
package cn.jianchengwang.tl.poi.excel.reader;

import cn.jianchengwang.tl.common.S;
import cn.jianchengwang.tl.poi.excel.Reader;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.ExtraInfo;
import cn.jianchengwang.tl.poi.excel.config.extrainfo.Info;
import cn.jianchengwang.tl.poi.excel.converter.Converter;
import cn.jianchengwang.tl.poi.excel.exception.ReaderException;
import cn.jianchengwang.tl.poi.excel.config.GridSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ReaderWith2003 extends ReaderConverterAndValidator implements ExcelReader {

    private Workbook workbook;

    public ReaderWith2003(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public <T> void readExcel(Reader reader) throws ReaderException {
        Class             clazz    = reader.gridSheet().clazz();
        Stream.Builder<GridSheet<T>> builder = Stream.builder();

        try {
            this.initFieldConverterAndValidator(clazz.getDeclaredFields());

            ExtraInfo extraInfo = reader.gridSheet().extraInfo();

            boolean haveExtraInfo = false;
            int extraInfoRow = 0;
            int extraInfoCol = 0;
            int extraInfoColSpan = 0;
            if(extraInfo!=null && extraInfo.haveExtraInfo()) {
                extraInfoRow = extraInfo.getRow();
                extraInfoCol = extraInfo.getCol();
                extraInfoColSpan = extraInfo.colSpan();
            }

            boolean isGetSingleSheet = S.isNotEmpty(reader.gridSheet().sheetName()) || reader.gridSheet().sheetIndex()>-1;
            for(int si=0; si<workbook.getNumberOfSheets(); si++) {

                Sheet sheet = workbook.getSheetAt(si);
                if(isGetSingleSheet) {
                    sheet = getSheet(reader);
                }

                GridSheet<T> gridSheet = GridSheet.build().clazz(clazz).sheetIndex(si).sheetName(sheet.getSheetName()).extraInfo(reader.gridSheet().extraInfo());
                int startRow = reader.gridSheet().getStartRow();
                if(haveExtraInfo) {

                    List<Info> infoList = gridSheet.extraInfo().infoList();
                    for(int ri=0; ri<extraInfoRow; ri++) {
                        Row row = sheet.getRow(ri + startRow);
                        if (null == row) {
                            continue;
                        }

                        for(int ci=0; ci<extraInfoCol; ci++) {

                            Cell cellTitle = row.getCell(ci + (extraInfoColSpan+1) * ci);
                            Cell cellMsg = row.getCell(ci + (extraInfoColSpan+1) * ci + 1);

                            Info info = infoList.get(ri);
                            info.setK(getCellValue(cellTitle));
                            info.setV(getCellValue(cellMsg));

                        }
                    }

                    startRow = startRow + extraInfoRow + 1 + reader.gridSheet().headLineRow();
                } else {
                    startRow = startRow + reader.gridSheet().headLineRow();
                }
                int totalRow = sheet.getPhysicalNumberOfRows();

                List<T> data = new ArrayList<>();
                for (int ri = startRow; ri <= totalRow; ri++) {

                    Row row = sheet.getRow(ri);
                    if (null == row) {
                        continue;
                    }

                    Object instance = clazz.newInstance();
                    for (Field field : fieldIndexes.values()) {
                        this.writeFiledValue(row, instance, field, reader.recordErrorMsg());
                    }

                    data.add((T) instance);
                }

                gridSheet.data(data);

                builder.add(gridSheet);

                if(isGetSingleSheet) break;
            }

            reader.setGridSheetStream(builder.build());

        } catch (Exception e) {
            e.printStackTrace();
            throw new ReaderException(e);
        }
    }

    public Sheet getSheet(Reader reader) {
        return S.isNotEmpty(reader.gridSheet().sheetName()) ?
                workbook.getSheet(reader.gridSheet().sheetName()) : workbook.getSheetAt(reader.gridSheet().sheetIndex());
    }

    public Object getCellValue(Field field, Cell cell) {
        Converter<String, ?> converter = fieldConverters.get(field);

        if (null == converter) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() != CellType.NUMERIC) {
            return converter.stringToR(cell.getStringCellValue(), field.getType());
        }
        if (isDateType(field.getType())) {
            Date javaDate = DateUtil.getJavaDate(cell.getNumericCellValue());
            if (field.getType().equals(Date.class)) {
                return javaDate;
            } else if (field.getType().equals(LocalDate.class)) {
                return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (field.getType().equals(LocalDateTime.class)) {
                return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            return null;
        } else {
            return converter.stringToR(cell.getNumericCellValue() + "", field.getType());
        }
    }

    public String getCellValue(Cell cell) {

        if(cell == null) return "";

        if(cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }

        return cell.getStringCellValue();
    }

    private boolean isDateType(Class<?> type) {
        return Date.class.equals(type) || LocalDate.class.equals(type) || LocalDateTime.class.equals(type);
    }
}
