package com.edsdev.jconvert.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * This class is used to load String resources specific to the current Locale
 * 
 * @author Ed Sarrazin Created on Sep 26, 2007 7:50:47 PM
 */
public class Messages {
    private static ResourceBundle bundle = null;

    static {
        bundle = ResourceBundle.getBundle("jcMessages");
    }

    public static void resetBundle() {
        bundle = ResourceBundle.getBundle("jcMessages");
    }

    /**
     * Gets the resource specific to the specified key
     * 
     * @param key String value key that you are looking up
     * @return String result
     */
    public static String getResource(String key) {
        return bundle.getString(key);
    }

    /**
     * Gets the resource specific to the specified key
     * 
     * @param key String value key that you are looking up
     * @param replacementValue replaces the first parameter in the string result with this value
     * @return String result
     */
    public static String getResource(String key, String replacementValue) {
        String rv = bundle.getString(key);
        rv = MessageFormat.format(rv, new Object[] { replacementValue });
        return rv;
    }

    /**
     * Gets the resource specific to the specified key, replacing the parmeters in the string with the two passed in
     * values
     * 
     * @param key String value key that you are looking up
     * @param value1 replaces the first parameter in the string result with this value
     * @param value2 replaces the second parameter in the string result with this value
     * @return String result
     */
    public static String getResource(String key, String value1, String value2) {
        String rv = bundle.getString(key);
        rv = MessageFormat.format(rv, new Object[] { value1, value2 });
        return rv;
    }

    /**
     * Gets the resource specific to the specified key, replacing the parmeters in the string with the values passed in
     * 
     * @param key String value key that you are looking up
     * @param values Object array of values to replace in the string result.
     * @return String result
     */
    public static String getResource(String key, Object[] values) {
        String rv = bundle.getString(key);
        rv = MessageFormat.format(rv, values);
        return rv;
    }

    /**
     * Specialized translation designed to handle missing resources in the bundle. This searched for the key, and if the
     * key is not found, then the key is returned. This basically handles translating the actual units and if there is
     * no translation found, it just uses the value in the convert file.
     * 
     * @param key String value key that you are looking up
     * @return String result
     */
    public static String getUnitTranslation(String key) {
        String rv = key;
        try {
            rv = bundle.getString(key);
        } catch (Exception e) {
            //do nothing
        }
        return rv;
    }
}
