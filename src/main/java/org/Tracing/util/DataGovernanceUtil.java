package org.Tracing.util;

import java.util.regex.Pattern;

public class DataGovernanceUtil {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern ID_PATTERN = Pattern.compile("^[0-9]{17}[0-9Xx]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private DataGovernanceUtil() {
    }

    public static String maskValue(String type, String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return rawValue;
        }

        if ("phone".equalsIgnoreCase(type)) {
            if (rawValue.length() < 7) {
                return rawValue;
            }
            return rawValue.substring(0, 3) + "****" + rawValue.substring(rawValue.length() - 4);
        }

        if ("id_card".equalsIgnoreCase(type)) {
            if (rawValue.length() < 8) {
                return rawValue;
            }
            return rawValue.substring(0, 3) + "***********" + rawValue.substring(rawValue.length() - 4);
        }

        if ("email".equalsIgnoreCase(type)) {
            int atIndex = rawValue.indexOf("@");
            if (atIndex <= 1) {
                return "***" + rawValue.substring(Math.max(atIndex, 0));
            }
            String prefix = rawValue.substring(0, atIndex);
            String suffix = rawValue.substring(atIndex);
            return prefix.charAt(0) + "***" + suffix;
        }

        return rawValue;
    }

    public static boolean isFormatValid(String formatType, String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        if ("phone".equalsIgnoreCase(formatType)) {
            return PHONE_PATTERN.matcher(value).matches();
        }
        if ("id_card".equalsIgnoreCase(formatType)) {
            return ID_PATTERN.matcher(value).matches();
        }
        if ("email".equalsIgnoreCase(formatType)) {
            return EMAIL_PATTERN.matcher(value).matches();
        }

        return false;
    }
}
