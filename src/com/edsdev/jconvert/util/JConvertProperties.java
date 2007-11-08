package com.edsdev.jconvert.util;

import java.util.Properties;

/**
 * "Static" class that represents the jconvert properties file. Will automatically load the file for you and has
 * convenience methods for the commonly used properties.
 * 
 * @author Ed Sarrazin Created on Sep 18, 2007 6:55:41 AM
 */
public class JConvertProperties {
    private static Properties props = null;

    public static final String MAJOR_VERSION = "MajorVersion";

    public static final String MINOR_VERSION = "MinorVersion";

    public static final String REVISION = "Revision";

    public static final String APP_NAME = "ApplicationName";

    public static final String BUILD_DATE = "BuildDate";

    private static final Logger log = Logger.getInstance(JConvertProperties.class);

    /** Static initializer - lets do this once */
    static {
        try {
            props = ResourceManager.loadProperties("jconvert.properties");
        } catch (Exception e) {
            log.error("Failed to load the jconvert properties.", e);
        }
    }

    private JConvertProperties() {
        //not public
    }

    public static String getMajorVersion() {
        return props.getProperty(MAJOR_VERSION);
    }

    public static String getMinorVersion() {
        return props.getProperty(MINOR_VERSION);
    }

    public static String getRevision() {
        return props.getProperty(REVISION);
    }

    public static String getAppName() {
        return props.getProperty(APP_NAME);
    }

    public static String getBuildDate() {
        return props.getProperty(BUILD_DATE);
    }

    public static String getProp(String propName) {
        return props.getProperty(propName);
    }

    public static String getBuidVersion() {
        return getMajorVersion() + "." + getMinorVersion() + "." + getRevision();
    }
}
