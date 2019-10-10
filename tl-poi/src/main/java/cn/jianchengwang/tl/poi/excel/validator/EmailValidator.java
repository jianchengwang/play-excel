package cn.jianchengwang.tl.poi.excel.validator;

import cn.jianchengwang.tl.common.V;

public class EmailValidator implements Validator {
    @Override
    public String valid(Object value) {
        String valueString = (String) value;
        return V.isEmail(valueString) ? null : "[" + valueString + "]不是正确的EMail.";
    }
}
