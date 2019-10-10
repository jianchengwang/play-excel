package cn.jianchengwang.tl.storage;

import cn.bespinglobal.amg.common.tool.S;

import java.util.Calendar;

/**
 * Created by wjc on 2019/8/28
 **/
public interface KeyGenerator {

    default String getPrefixTmpl(String prefix, String tmpl) {
       if(S.isNotBlank(prefix)) {
           return "%3$s/" + tmpl;
       }
       return tmpl;
    }

    String getKey(String name, KeyProvider keyNameProvider, String module, String prefix, Boolean useFileNameAsKeyName);

    enum Predefined implements KeyGenerator {

        /**
         * All item stored in the bucket (root folder) without hierarchy
         */
        PLAIN {
            @Override
            protected String tmpl(String prefix) {
                return null;
            }
        },
        /**
         * Items stored in a hierarchy structured by date: prefix/yyyy/MM/dd/item
         */
        BY_DATE {
            @Override
            protected String tmpl(String prefix) {
                return getPrefixTmpl(prefix, "%1$tY/%1$tm/%1$td/%2$s");
            }
        },
        /**
         * Items stored in a hierarchy structured by date and time: prefix/yyyy/MM/dd/HH/item
         */
        BY_HOUR {
            @Override
            protected String tmpl(String prefix) {
                return getPrefixTmpl(prefix,"%1$tY/%1$tm/%1$td/%1$tH/%2$s");
            }
        },
        /**
         * Items stored in a hierarchy structured by date and time: prefix/yyyy/MM/dd/HH/mm/item
         */
        BY_MINUTE {
            @Override
            protected String tmpl(String prefix) {
                return getPrefixTmpl(prefix,"%1$tY/%1$tm/%1$td/%1$tH/%1$tM/%2$s");
            }
        },
        /**
         * Items stored in a hierarchy structured by date and time: prefix/yyyy/MM/dd/HH/mm/ss/item
         */
        BY_SECOND {
            @Override
            protected String tmpl(String prefix) {
                return getPrefixTmpl(prefix,"%1$tY/%1$tm/%1$td/%1$tH/%1$tM/%1$tS/%2$s");
            }
        },
        /**
         * Items stored in a hierarchy structured by date and time: prefix/yyyy/MM/dd/HH/mm/ss/item
         * <p>
         * Note this enum value is deprecated, please use `BY_SECOND` instead
         */
        @Deprecated
        BY_DATETIME {
            @Override
            protected String tmpl(String prefix) {
                return getPrefixTmpl(prefix,"%1$tY/%1$tm/%1$td/%1$tH/%1$tM/%1$tS/%2$s");
            }
        };

        protected abstract String tmpl(String prefix);

        public String getKey(String name, KeyProvider keyProvider, String module, String prefix, Boolean useFileNameAsKeyName) {
            String prefix_ = "";

            if (keyProvider != null) {
                prefix_ = keyProvider.newPrefixName(module, prefix);

                // useFileNameAsKeyName is false，provider a new key name
                if(!useFileNameAsKeyName) {
                    name = keyProvider.newKeyName(name);
                }
            }
            String tmpl = tmpl(prefix_);
            if (S.blank(tmpl)) {
                return name;
            } else {
                return S.fmt(tmpl, Calendar.getInstance(), name, prefix_);
            }
        }
    }
}
