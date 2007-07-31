package com.edsdev.jconvert.util;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author Ed Sarrazin Created on Jul 28, 2007 5:54:33 PM
 */
public class Logger {
    private Class clazz;

    private Object log4jLogger = null;

    private static boolean searchedForLog4j = false;

    public static Logger getInstance(Class pClazz) {
        return new Logger(pClazz);
    }

    public static void main(String[] args) {
        Logger log = Logger.getInstance(Logger.class);
        log.debug("This is a test");
        log.error("This is an error");
    }

    public Logger(Class pClazz) {
        clazz = pClazz;
        if (!searchedForLog4j) {
            searchedForLog4j = true;
            try {
                Class theClass = Class.forName("org.apache.log4j.Logger");
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

    public void debug(Object message) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - DEBUG - " + clazz.getName() + " - " + message);
        } else {
            invokeLoggerMethod("debug", message);
        }
    }

    public void info(Object message) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - INFO - " + clazz.getName() + " - " + message);
        } else {
            invokeLoggerMethod("info", message);
        }
    }

    public void warn(Object message) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - WARN - " + clazz.getName() + " - " + message);
        } else {
            invokeLoggerMethod("warn", message);
        }
    }

    public void warn(Object message, Throwable t) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - WARN - " + clazz.getName() + " - " + message);
            t.printStackTrace();
        } else {
            invokeLoggerMethod("warn", message, t);
        }
    }

    public void error(Object message) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - ERROR - " + clazz.getName() + " - " + message);
        } else {
            invokeLoggerMethod("error", message);
        }
    }

    public void error(Object message, Throwable t) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - ERROR - " + clazz.getName() + " - " + message);
            t.printStackTrace();
        } else {
            invokeLoggerMethod("error", message);
        }
    }

    public void fatal(Object message) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - FATAL - " + clazz.getName() + " - " + message);
        } else {
            invokeLoggerMethod("fatal", message);
        }
    }

    public void fatal(Object message, Throwable t) {
        if (log4jLogger == null) {
            System.out.println(new Date() + " - FATAL - " + clazz.getName() + " - " + message);
            t.printStackTrace();
        } else {
            invokeLoggerMethod("fatal", message);
        }
    }
}
