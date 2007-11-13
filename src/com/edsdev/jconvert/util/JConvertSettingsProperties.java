package com.edsdev.jconvert.util;

import java.io.FileOutputStream;
import java.util.Properties;

/**
 * "Static" class that represents the settings of the application that could be configured by the user.
 * 
 * @author Ed Sarrazin Created on Sep 19, 2007 4:27:45 PM
 */
public class JConvertSettingsProperties {
    private static Properties props = null;

    private static final Logger log = Logger.getInstance(JConvertSettingsProperties.class);

    private static final String FILE_NAME = "jconvert_settings.properties";

    public static final String APP_WIDTH = "ApplicationWidth";

    public static final String APP_HEIGHT = "ApplicationHeight";

    public static final String APP_X = "ApplicationX";

    public static final String APP_Y = "ApplicationY";

    public static final String HIDDEN_TABS = "HiddenTabs";

    public static final String LAST_TAB = "LastTab";

    public static final String LAST_VALUE = "LastValue";

    public static final String LAST_FROM = "LastFrom";

    public static final String LAST_TO = "LastTo";

    public static final String LOCALE_LANGUAGE = "LocaleLanguage";

    public static final String LOCALE_COUNTRY = "LocaleCountry";

    public static final String LOCALE_VARIANT = "LocaleVariant";

    public static final String CHECK_FOR_NEWER_VERSION = "CheckForNewerVersion";

    public static final String CUSTOM_CONVERSION_CLASS = "CustomConversionClass";

    public static final String CUSTOM_CONVERSION_JAR = "CustomConversionJar";

    public static final String LOG_LEVEL = "LogLevel";

    /** Static initializer - lets do this once */
    static {
        try {
            props = ResourceManager.loadProperties(FILE_NAME);
            setDefaults();
        } catch (Exception e) {
            props = new Properties();
            setDefaults();
            log.warn("Cannot load settings: " + FILE_NAME + " not found.");
        }
    }

    private static void setDefaults() {
        if (getCheckForNewerVersion() == null) {
            setCheckForNewerVersion("true");
        }
        if (getCustomConversionClass() == null) {
            setCustomConversionClass("com.edsdev.jconvert.common.CustomConversionCurrency");
        }
        if (getLogLevel() == null) {
            setLogLevel("DEBUG");
        }
    }

    private static String getFilePath() {
        String jarPath = ResourceManager.getJarPath();
        return jarPath + FILE_NAME;
    }

    public static void persist() {
        try {
            FileOutputStream fos = new FileOutputStream(getFilePath());
            props.store(fos, "Jconvert Settings File");
        } catch (Exception e) {
            log.error("Failed to save jconvert settings.", e);
        }
    }

    private JConvertSettingsProperties() {
        //not public
    }

    public static String getProp(String propName) {
        return props.getProperty(propName);
    }

    public static String getAppWidth() {
        return props.getProperty(APP_WIDTH);
    }

    public static void setAppWidth(String val) {
        props.setProperty(APP_WIDTH, val);
    }

    public static String getAppHeight() {
        return props.getProperty(APP_HEIGHT);
    }

    public static void setAppHeight(String val) {
        props.setProperty(APP_HEIGHT, val);
    }

    public static String getAppX() {
        return props.getProperty(APP_X);
    }

    public static void setAppX(String val) {
        props.setProperty(APP_X, val);
    }

    public static String getAppY() {
        return props.getProperty(APP_Y);
    }

    public static void setAppY(String val) {
        props.setProperty(APP_Y, val);
    }

    public static String getHiddenTabs() {
        return props.getProperty(HIDDEN_TABS);
    }

    public static void setHiddenTabs(String val) {
        props.setProperty(HIDDEN_TABS, val);
    }

    public static String getLastTab() {
        return props.getProperty(LAST_TAB);
    }

    public static void setLastTab(String val) {
        props.setProperty(LAST_TAB, val);
    }

    public static String getLastValue() {
        return props.getProperty(LAST_VALUE);
    }

    public static void setLastValue(String val) {
        props.setProperty(LAST_VALUE, val);
    }

    public static String getLastFrom() {
        return props.getProperty(LAST_FROM);
    }

    public static void setLastFrom(String val) {
        props.setProperty(LAST_FROM, val);
    }

    public static String getLastTo() {
        return props.getProperty(LAST_TO);
    }

    public static void setLastTo(String val) {
        props.setProperty(LAST_TO, val);
    }

    public static String getLocaleLanguage() {
        return props.getProperty(LOCALE_LANGUAGE);
    }

    public static void setLocaleLanguage(String val) {
        props.setProperty(LOCALE_LANGUAGE, val);
    }

    public static String getLocaleCountry() {
        return props.getProperty(LOCALE_COUNTRY);
    }

    public static void setLocaleCountry(String val) {
        props.setProperty(LOCALE_COUNTRY, val);
    }

    public static String getLocaleVariant() {
        return props.getProperty(LOCALE_VARIANT);
    }

    public static void setLocaleVariant(String val) {
        props.setProperty(LOCALE_VARIANT, val);
    }

    public static String getCheckForNewerVersion() {
        return props.getProperty(CHECK_FOR_NEWER_VERSION);
    }

    public static void setCheckForNewerVersion(String val) {
        props.setProperty(CHECK_FOR_NEWER_VERSION, val);
    }

    public static String getCustomConversionClass() {
        return props.getProperty(CUSTOM_CONVERSION_CLASS);
    }

    public static void setCustomConversionClass(String val) {
        props.setProperty(CUSTOM_CONVERSION_CLASS, val);
    }

    public static String getCustomConversionJar() {
        return props.getProperty(CUSTOM_CONVERSION_JAR);
    }

    public static void setCustomConversionJar(String val) {
        props.setProperty(CUSTOM_CONVERSION_JAR, val);
    }

    public static String getLogLevel() {
        return props.getProperty(LOG_LEVEL);
    }

    public static void setLogLevel(String val) {
        props.setProperty(LOG_LEVEL, val);
    }
}
