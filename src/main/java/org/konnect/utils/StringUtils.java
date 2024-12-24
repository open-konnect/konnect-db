package org.konnect.utils;

import java.util.Arrays;

public class StringUtils {

    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (css == null || css.length == 0) {
            return false;
        }
        return Arrays.stream(css).anyMatch(StringUtils::isBlank);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int len = length(cs);
        if (len == 0) {
            return true;
        }
        for(int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String defaultIfBlank(String givenStr, String defaultVal) {
        return isBlank(givenStr) ? defaultVal : givenStr;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) return true;
        if (length(cs1) != length(cs2)) return false;
        for (int i = 0; i < cs1.length(); i++) {
            char ch1 = Character.toUpperCase(cs1.charAt(i));
            char ch2 = Character.toUpperCase(cs2.charAt(i));
            if (ch1 != ch2) return false;
        }
        return true;
    }
}
