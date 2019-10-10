package cn.jianchengwang.tl.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

/**
 * String Tool
 * Created by wjc on 2019/8/26
 **/
@UtilityClass
public class S {

    public static String string(Object o) {
        if (null == o) {
            return "";
        }
        return o.toString();
    }

    /**
     * A handy alias for {@link String#format(String, Object...)}
     *
     * @param tmpl the message template
     * @param args the message arguments
     * @return the formatted string
     */
    public final static String fmt(String tmpl, Object... args) {
        if (0 == args.length) return tmpl;
        return String.format(tmpl, args);
    }

    /**
     * Returns the template or `""` if template is `null`
     * @param template a string
     * @return the template or `""` if template is `null`
     */
    public static String msgFmt(String template) {
        return S.string(template);
    }

    /**
     * A handy alias for {@link MessageFormat#format(String, Object...)}
     *
     * @param template the message template
     * @param args the message arguments
     * @return the formatted string or `""` if template is `null`
     */
    public static String msgFmt(String template, Object... args) {
        if (0 == args.length) return template;
        if (null == template) return "";
        return MessageFormat.format(template, args);
    }

    /**
     * alias of {@link #empty(String)}
     *
     * @param s the string to be checked
     * @return true if `s` is `null` or `empty`
     */
    public static boolean isEmpty(String s) {
        return empty(s);
    }

    /**
     * Determine if a string is empty or null
     *
     * @param s the string to be checked
     * @return true if the string is null or empty (no spaces)
     */
    public static boolean empty(String s) {
        return (null == s || "".equals(s));
    }

    public static boolean isNotEmpty(String s) {
        return notEmpty(s);
    }

    /**
     * Antonym of {@link #empty(String)}
     *
     * @param s the string to be checked
     * @return true if <code>s</code> is not <code>null</code> or empty
     */
    public static boolean notEmpty(String s) {
        return !empty(s);
    }

    /**
     * Determine if a string is all blank or empty or null
     *
     * @param s the string to be checked
     * @return true if the string is null or empty or all blanks
     */
    public static boolean blank(String s) {
        return (null == s || "".equals(s.trim()));
    }

    /**
     * alias of {@link #blank(String)}
     *
     * @param s the string to be checked
     * @return true if `s` is `null` or empty or blank
     */
    public static boolean isBlank(String s) {
        return blank(s);
    }

    public static boolean isNotBlank(String s) {
        return !blank(s);
    }


    /**
     * equal modifier: specify {@link #equal(String, String, int) equal} comparison
     * should ignore leading and after spaces. i.e. it will call <code>trim()</code>
     * method on strings before comparison
     */
    public static final int IGNORECASE = 0x00001000;

    /**
     * equal modifier: specify {@link #equal(String, String, int) equal} comparison
     * should be case insensitive
     */
    public static final int IGNORESPACE = 0x00002000;
    /**
     * alias of {@link #equal(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean eq(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Alias of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       String 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return `true` if `s1` equals to `s2` according to `modifier`
     */
    public static boolean eq(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }

    /**
     * Antonym of {@link #equal(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 doesn't equal to s2
     */
    public static boolean neq(String s1, String s2) {
        return !equal(s1, s2);
    }

    /**
     * Antonym of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       string 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return <code>true</code> if s1 doesn't equal to s2 as per modifier
     */
    public static boolean neq(String s1, String s2, int modifier) {
        return !equal(s1, s2, modifier);
    }

    /**
     * Return true if 2 strings are equals to each other without
     * ignore space and case sensitive.
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean equal(String s1, String s2) {
        return equal(s1, s2, 0);
    }

    /**
     * Return false if 2 strings are equals to each other
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return `true` if s1 does not equal to s2
     */
    public static boolean notEqual(String s1, String s2) {
        return !equal(s1, s2, 0);
    }

    /**
     * alias of {@link #eq(String, String)}
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return <code>true</code> if s1 equals to s2
     */
    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Return true if 2 strings are equals to each other as per rule specified
     *
     * @param s1       string 1
     * @param s2       String 2
     * @param modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return `true` if `s1` equals to `s2` according to `modifier`
     */
    public static boolean equal(String s1, String s2, int modifier) {
        if (null == s1) {
            return s2 == null;
        }
        if (null == s2)
            return false;
        if ((modifier & IGNORESPACE) != 0) {
            s1 = s1.trim();
            s2 = s2.trim();
        }
        if ((modifier & IGNORECASE) != 0) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return s1.equals(s2);
        }
    }

    /**
     * Check if all strings are equal to each other
     *
     * @param modifier specify whether ignore space or case sensitive
     * @param sa       the list of strings
     * @return <code>true</code> if all strings are equal to each other as per modifier specified
     */
    public static boolean equal(int modifier, String... sa) {
        int len = sa.length;
        if (len < 2) {
            throw new IllegalArgumentException("At least 2 strings required");
        }
        String s = sa[0];
        for (int i = 1; i < len; ++i) {
            String s1 = sa[i];
            if (!equal(s, s1, modifier)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alias of {@link #equal(String, String, int)}
     *
     * @param s1       string 1
     * @param s2       string 2
     * @param modifier the modifier could be combination of {@link #IGNORESPACE} or {@link #IGNORECASE}
     * @return <code>true</code> if s1 equals to s2 as per modifier
     */
    public static boolean isEqual(String s1, String s2, int modifier) {
        return equal(s1, s2, modifier);
    }
}
