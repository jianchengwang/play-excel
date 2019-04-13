package cn.jianchengwang.tl.poi.excel.validator;

import cn.jianchengwang.tl.poi.excel.kit.ValidatorKit;

public class EmailValidator implements Validator {
    @Override
    public String valid(Object value) {
        String valueString = (String) value;
        return ValidatorKit.isEmail(valueString) ? null : "[" + valueString + "]不是正确的EMail.";
    }
}
