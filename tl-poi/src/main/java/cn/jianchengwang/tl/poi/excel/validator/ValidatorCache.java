package cn.jianchengwang.tl.poi.excel.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjc on 2019/9/17
 **/
public class ValidatorCache {

    private static final Map<Class<? extends Validator>, Validator> VALIDATOR_MAP = new HashMap<>(64);

    static {
        VALIDATOR_MAP.put(EmailValidator.class, new EmailValidator());
        VALIDATOR_MAP.put(MobileValidator.class, new MobileValidator());
    }

    public static void addValidator(Validator validator) {
        if (null != validator) {
            VALIDATOR_MAP.put(validator.getClass(), validator);
        }
    }

    public static Validator getValidator(
            Class<? extends Validator> type) {
        return VALIDATOR_MAP.get(type);
    }
}
