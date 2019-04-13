package cn.jianchengwang.tl.poi.excel.validator;

public interface Validator {
    /**
     * 验证单元格的值, 若验证失败, 请返回错误消息.
     *
     * @param value 当前单元格的值
     * @return null or errorMessage
     */
    String valid(Object value);

}
