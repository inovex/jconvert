package com.edsdev.jconvert.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class is used to load String resources specific to the current Locale
 * 
 * @author Ed S Created on Sep 26, 2007 7:50:47 PM
 */
public class Messages {
    private static ResourceBundle bundle = null;

    static {
        Locale.setDefault(new Locale("xy", "XY"));
        bundle = ResourceBundle.getBundle("jcMessages");
    }

    /**
     * Gets the resource specific to the specified key
     * 
     * @param key
     * @return
     */
    public static String getResource(String key) {
        return bundle.getString(key);
    }

    /**
     * Gets the resource specific to the specified key
     * 
     * @param key
     * @param replacementValue replaces the first parameter in the string result with this value
     * @return
     */
    public static String getResource(String key, String replacementValue) {
        String rv = bundle.getString(key);
        MessageFormat.format(key, new Object[] { replacementValue });
        return rv;
    }

    /**
     * Gets the resource specific to the specified key, replacing the parmeters in the string with the two passed in
     * values
     * 
     * @param key
     * @param value1
     * @param value2
     * @return
     */
    public static String getResource(String key, String value1, String value2) {
        String rv = bundle.getString(key);
        MessageFormat.format(key, new Object[] { value1, value2 });
        return rv;
    }

    /**
     * Specialized translation designed to handle missing resources in the bundle. This searched for the key, and if the
     * key is not found, then the key is returned. This basically handles translating the actual units and if there is
     * no translation found, it just uses the value in the convert file.
     * 
     * @param key
     * @return
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
