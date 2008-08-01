package com.edsdev.jconvert.util;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * This logger is intended to interface with log4j if it can be found in the classpath. If it cannot, then we have our
 * own formatting with using System.out. The whole purpose of this is to allow this jar to run independently on a user's
 * machine without dependencies on anything else. This way there are no other complicated instructions for the average
 * user.
 * 
 * @author Ed Sarrazin Created on Jul 28, 2007 5:54:33 PM
 */
public class Logger {
    /**
     * Class/category you will be logging for
     */
    private Class clazz;

    /**
     * Handle to log4j logger if one is found
     */
    private Object log4jLogger = null;

    /**
     * Have we attempted to find log4j. Allows us to look only once
     */
    private static boolean searchedForLog4j = false;

    private static final String LOG4J_PATH = "org.apache.log4j.Logger";

    private static final String DEBUG = "debug";

    private static final String INFO = "info";

    private static final String WARN = "warn";

    private static final String ERROR = "error";

    private static final String FATAL = "fatal";

    public static Logger getInstance(Class pClazz) {
        return new Logger(pClazz);
    }

    public Logger(Class pClazz) {
        clazz = pClazz;
        if (!searchedForLog4j) {
            searchedForLog4j = true;
            try {
                Class theClass = Class.forName(LOG4J_PATH);
                Method meth = theClass.getMethod("getLogger", new Class[] { Class.class });
                log4jLogger = meth.invoke(null, new Object[] { clazz });
            } catch (Exception e) {
                System.out.println("Log4j not found in path, so using System.out.");
            }

        }
    }

    private void invokeLoggerMethod(String methodName, Object message) {
        try {
            Method method = log4jLogger.getClass().getMethod(methodName, new Class[] { Object.class });
            method.invoke(log4jLogger, new Object[] { message });
        } catch (Exception e) {
            System.out.println("Failed to log using the logger. Message is :" + message);
        }
    }

    private void invokeLoggerMethod(String methodName, Object message, Throwable t) {
        try {
            Method method = log4jLogger.getClass().getMethod(methodName, new Class[] { Object.class, Throwable.class });
            method.invoke(log4jLogger, new Object[] { message, t });
        } catch (Exception e) {
            System.out.println("Failed to log using the logger. Message is :" + message);
            t.printStackTrace();
        }
    }

    private void printMessage(String type, Object message, Throwable t) {
        if (log4jLogger == null) {
            if (shouldLog(type)) {
                System.out.println(new Date() + " - " + type.toUpperCase() + " - " + clazz.getName() + " - " + message);
                if (t != null) {
                    t.printStackTrace();
                }
            }
        } else {
            if (t == null) {
                invokeLoggerMethod(type, message);
            } else {
                invokeLoggerMethod(type, message, t);
            }
        }
    }

    private boolean shouldLog(String type) {
        String logLevel = "";
        try {
            logLevel = JConvertSettingsProperties.getLogLevel();
        } catch (Exception e) {
            return true;
        }
        if (logLevel.equalsIgnoreCase(DEBUG)) {
            return true;
        }
        if (logLevel.equalsIgnoreCase(INFO)) {
            return !(type.equalsIgnoreCase(DEBUG));
        }
        if (logLevel.equalsIgnoreCase(WARN)) {
            return !(type.equalsIgnoreCase(DEBUG) || type.equalsIgnoreCase(INFO));
        }
        if (logLevel.equalsIgnoreCase(ERROR)) {
            return (type.equalsIgnoreCase(ERROR) || type.equalsIgnoreCase(FATAL));
        }
        if (logLevel.equalsIgnoreCase(FATAL)) {
            return (type.equalsIgnoreCase(FATAL));
        }
        return false;
    }

    public void debug(Object message) {
        printMessage(DEBUG, message, null);
    }

    public void info(Object message) {
        printMessage(INFO, message, null);
    }

    public void warn(Object message) {
        printMessage(WARN, message, null);
    }

    public void warn(Object message, Throwable t) {
        printMessage(DEBUG, message, t);
    }

    public void error(Object message) {
        printMessage(ERROR, message, null);
    }

    public void error(Object message, Throwable t) {
        printMessage(ERROR, message, t);
    }

    public void fatal(Object message) {
        printMessage(FATAL, message, null);
    }

    public void fatal(Object message, Throwable t) {
        printMessage(DEBUG, message, t);
    }
}
