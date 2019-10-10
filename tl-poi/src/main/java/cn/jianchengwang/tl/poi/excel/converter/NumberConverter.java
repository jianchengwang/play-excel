package cn.jianchengwang.tl.poi.excel.converter;


public abstract class NumberConverter {

    String replaceComma(String value) {

        if (value == null || value.trim().length() == 0) {
            return null;
        }
        value = value.replaceAll(",", "");
        if(value.endsWith(".0")){
            return value.substring(0, value.length() - 2);
        }
        return value;
    }

}
