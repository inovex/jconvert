/*
 * Created on Mar 8, 2004 To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package com.edsdev.jconvert.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import javax.swing.ImageIcon;

/**
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 * 
 * @author Ed Sarrazin
 */
public class ResourceManager {
    private static Logger log = Logger.getInstance(ResourceManager.class);

    /**
     * Loads and Image from the classpath as a resource
     * 
     * @param imageRelativeURL relative url of the image you want to get
     * @return Image object
     */
    public static Image getImage(String imageRelativeURL) {
        Image image = null;
        try {
            ClassLoader cl = ResourceManager.class.getClassLoader();
            image = Toolkit.getDefaultToolkit().getImage(cl.getResource(imageRelativeURL));
        } catch (Exception exp) {
            log.error("Failed to get Image " + imageRelativeURL, exp);
        }
        return image;
    }

    /**
     * Loads a properties file from the classpath based on the name provided
     * 
     * @param filename Name of the file you want to load
     * @return Properties object
     * @throws java.io.IOException
     */
    public static Properties loadProperties(String filename) throws java.io.IOException {
        return loadProperties(getResourceAsStream(filename));
    }

    /**
     * Attempts to find a resource in the classpath and returns an InputStream to that resrouce
     * 
     * @param resourceName name of the resource you want to get as a stream
     * @return stream to the resource
     */
    public static InputStream getResourceAsStream(String resourceName) {
        InputStream is = null;

        ClassLoader cl = ResourceManager.class.getClassLoader();
        is = cl.getResourceAsStream(resourceName);
        if (is != null) {
            return is;
        }

        cl = Thread.currentThread().getContextClassLoader();
        is = cl.getResourceAsStream(resourceName);
        if (is != null) {
            return is;
        }

        //backup - if you cannot find the resource, then look for it physically in the jar path. Althought the jar path
        // should be in the classpath, this is a double check because depending on how you run this, it may not. Set
        // getJarPath code for more detail.
        try {
            is = new FileInputStream(getJarPath() + resourceName);
        } catch (Exception e) {
            //do nothing here
        }
        return is;
    }

    /**
     * Responsible for determining what the path is to the jar file.
     * 
     * @return String representation of the path
     */
    public static String getJarPath() {
        String rv = "";

        ClassLoader cl = ResourceManager.class.getClassLoader();

        try {
            String fileName = "jconvert-" + JConvertProperties.getMajorVersion() + "."
                    + JConvertProperties.getMinorVersion() + "." + JConvertProperties.getRevision() + ".jar";
            log.debug("Searching for file " + fileName + " to determine the jar path.");
            URL url = cl.getResource(fileName);
            if (url != null) {
                rv = url.getPath();
                rv = URLDecoder.decode(rv, System.getProperty("file.encoding"));
                rv = rv.substring(0, rv.indexOf(fileName));
            }
        } catch (Exception e) {
            log.error("Failed to get the path of the jar", e);
        }

        //may not find the jar, so we would still like to return some sort of consistent path, so we are going to
        // return the user.home.
        if (rv.equals("")) {
            rv = System.getProperty("user.home") + System.getProperty("file.separator");
        }

        return rv;
    }

    /**
     * Loads properties from an InputStream into a Properties object
     * 
     * @param Istream stream from which to load the properties file
     * @return Properties object
     * @throws java.io.IOException
     */
    public static Properties loadProperties(InputStream Istream) throws java.io.IOException {
        if (Istream == null) {
            throw new IOException("Can't load properties file from the inputstream - it is null");
        }

        Properties props = null;

        synchronized (ResourceManager.class) {
            try {
                props = new Properties();
                props.load(Istream);
                Istream.close();
            } catch (IOException e) {
                log.error("Can't load properties for Input stream", e);
                throw e;
            }
        }
        return props;
    }

    /**
     * Gets and ImageIcom from the classloader based on the image resource URL
     * 
     * @param imageRelativeURL relative url to load the image icon from
     * @return ImageIcon
     */
    public static ImageIcon getImageIcon(String imageRelativeURL) {
        ImageIcon icon = null;
        try {
            ClassLoader cl = ResourceManager.class.getClassLoader();
            icon = new ImageIcon(cl.getResource(imageRelativeURL));
        } catch (Exception exp) {
            log.error("Could not load image " + imageRelativeURL, exp);
        }
        return icon;
    }
}
