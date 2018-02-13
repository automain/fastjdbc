/*
 * Copyright 2018 fastjdbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fastjdbc.util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is only used to get request parameter.</p>
 * <p>String, Integer, Long, BigDecimal, Boolean, Timestamp, String[], List&lt;Integer&gt; are supported.</p>
 * <p>Double and Float can replace by BigDecimal, Date and Time can replace by Timestamp.</p>
 * <p>Every method can set default value except {@link #getStringValues(String, HttpServletRequest)}
 * and {@link #getIntegerValues(String, HttpServletRequest)}, if the parameter is null or empty,
 * the default value will be return.</p>
 */
public class RequestUtil {

    /**
     * Check string is null or empty.
     *
     * @param value the value to checked
     * @return {@code true} if the given string is null or empty, {@code false} otherwise
     */
    private static boolean isEmptyString(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Test string is numeric.
     *
     * @param value the value to be tested
     * @return {@code true} if the value is numeric, {@code false} otherwise
     */
    private static boolean isNumericString(String value) {
        if (isEmptyString(value)) {
            return false;
        }
        for (int i = value.length() - 1; i >= 0; i--) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get string from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty, otherwise return the value
     */
    public static String getString(String key, HttpServletRequest request) {
        return getString(key, request, null);
    }

    /**
     * Get string from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param defaultValue the default to return if the value is empty
     * @return default value if the value is empty, otherwise return the value
     */
    public static String getString(String key, HttpServletRequest request, String defaultValue) {
        String value = request.getParameter(key);
        value = value == null ? null : value.trim();
        return isEmptyString(value) ? defaultValue : value;
    }

    /**
     * Get Integer from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty or format exception occurred, otherwise return the value
     */
    public static Integer getInteger(String key, HttpServletRequest request) {
        return getInteger(key, request, null);
    }

    /**
     * Get Integer from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param defaultValue the default to return if the value is empty or format exception occurred
     * @return default value if the value is empty or format exception occurred, otherwise return the value
     */
    public static Integer getInteger(String key, HttpServletRequest request, Integer defaultValue) {
        String value = getString(key, request);
        if (isEmptyString(value)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get Long from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty or format exception occurred, otherwise return the value
     */
    public static Long getLong(String key, HttpServletRequest request) {
        return getLong(key, request, null);
    }

    /**
     * Get Long from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param defaultValue the default to return if the value is empty or format exception occurred
     * @return default value if the value is empty or format exception occurred, otherwise return the value
     */
    public static Long getLong(String key, HttpServletRequest request, Long defaultValue) {
        String value = getString(key, request);
        if (isEmptyString(value)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(key);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get BigDecimal from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty or format exception occurred, otherwise return the value
     */
    public static BigDecimal getBigDecimal(String key, HttpServletRequest request) {
        return getBigDecimal(key, request, null);
    }

    /**
     * Get BigDecimal from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param defaultValue the default to return if the value is empty or format exception occurred
     * @return default value if the value is empty or format exception occurred, otherwise return the value
     */
    public static BigDecimal getBigDecimal(String key, HttpServletRequest request, BigDecimal defaultValue) {
        String value = getString(key, request);
        if (isEmptyString(value)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get Boolean from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return {@code false} if the value is empty, otherwise return the value
     */
    public static Boolean getBoolean(String key, HttpServletRequest request) {
        return getBoolean(key, request, Boolean.FALSE);
    }

    /**
     * Get Boolean from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param defaultValue the default to return if the value is empty
     * @return default value if the value is empty, otherwise return the value
     */
    public static Boolean getBoolean(String key, HttpServletRequest request, Boolean defaultValue) {
        String value = getString(key, request);
        if (isEmptyString(value)) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Get Timestamp from request object, the default time format is {@code yyyy-MM-dd HH:mm:ss}.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty or parse exception occurred, otherwise return the value
     */
    public static Timestamp getTimestamp(String key, HttpServletRequest request) {
        return getTimestamp(key, request, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Get Timestamp from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @param pattern the time format pattern
     * @return null if the value is empty or parse exception occurred, otherwise return the value
     */
    public static Timestamp getTimestamp(String key, HttpServletRequest request, String pattern) {
        return getTimestamp(key, request, pattern, null);
    }

    /**
     * Get Timestamp from request object.
     *
     * @param key          the key of the value
     * @param request      the request object
     * @param pattern      the time format pattern
     * @param defaultValue the default to return if the value is empty or parse exception occurred
     * @return default value if the value is empty or parse exception occurred, otherwise return the value
     */
    public static Timestamp getTimestamp(String key, HttpServletRequest request, String pattern, Timestamp defaultValue) {
        String value = getString(key, request);
        if (isEmptyString(value)) {
            return defaultValue;
        }
        try {
            return new Timestamp(new SimpleDateFormat(pattern).parse(value).getTime());
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    /**
     * Get String array from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty, otherwise return an array of String
     */
    public static String[] getStringValues(String key, HttpServletRequest request) {
        return request.getParameterValues(key);
    }

    /**
     * Get Integer List from request object.
     *
     * @param key     the key of the value
     * @param request the request object
     * @return null if the value is empty, otherwise return the a List of Integer
     */
    public static List<Integer> getIntegerValues(String key, HttpServletRequest request) {
        String[] values = getStringValues(key, request);
        List<Integer> list = null;
        if (values != null) {
            list = new ArrayList<>(values.length);
            for (String value : values) {
                if (isNumericString(value)) {
                    list.add(Integer.valueOf(value));
                }
            }
        }
        return list;
    }
}
