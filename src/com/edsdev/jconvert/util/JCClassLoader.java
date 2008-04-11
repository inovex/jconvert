package com.edsdev.jconvert.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Used to load classes that are not in the classpath, by loading them from a specific directory/jar file. The class
 * will be loaded from the default classloaders first if possible, so do not use this mechanism to somehow override
 * existing classes that are in the classpath. This is a wrapper around java.net.URLClassLoader and only provides
 * convienince, sinplicity, safety, better exception processing, and better exception messages. <BR>
 * Example of use: <BR>
 * <blockquote>
 * 
 * <pre>
 * 
 *       ClassLoader loader = new JCClassLoader(&quot;i:/somepath/myjar-1.0.6.jar&quot;);
 *       Object main = loader.loadClass(&quot;com.mydomain.myproject.MyClass&quot;).newInstance();
 *       
 *       ClassLoader fileLoader = new JCClassLoader(&quot;i:/temp/&quot;);
 *       Object temp = fileLoader.loadClass(&quot;com.test.MyTestClass&quot;).newInstance()
 *  
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 5:00:57 PM
 */
public class JCClassLoader extends ClassLoader {
    private String path = "";

    /**
     * Constructs this class loader with the specified path. This path can be a directory (c:/test/), or it can be a
     * path to a jar file (c:/test/myjar.jar). Nulls will be transfrmed to an empty string.
     * 
     * @param pPath String representation of the directory or jar file
     */
    public JCClassLoader(String pPath) {
        super();
        path = pPath;
        if (path == null) {
            path = "";
        }
    }

    protected Class findClass(String className) throws ClassNotFoundException {
        try {
            // fix name for them if .class is appended to it
            if (className.endsWith(".class")) {
                className = className.substring(0, className.length() - 6);
            }
            URLClassLoader urlLoader = new URLClassLoader(new URL[] { new URL("file", null, path) });
            return Class.forName(className, true, urlLoader);
        } catch (Exception e) {
            // throw exception with message custom to jar loader
            if (path.endsWith(".jar")) {
                throw new ClassNotFoundException(className + " not found in " + path, e);
            }

            // throw exception with message custom to file loader
            String filePath = className;
            try {
                filePath = className.replaceAll("\\.", "/") + ".class";
            } catch (Exception ex) {
                // Do nothing - don't care, this is the least of my worries.
            }
            throw new ClassNotFoundException(className + " not found here: " + path + filePath, e);
        }
    }
}
