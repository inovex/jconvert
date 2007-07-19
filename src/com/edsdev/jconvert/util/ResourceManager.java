/*
 * Created on Mar 8, 2004 To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package com.edsdev.jconvert.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

/**
 * @author elsarrazin To change the template for this generated type comment go to Window - Preferences - Java - Code
 *         Generation - Code and Comments
 */
public class ResourceManager {
    public static Image getImage(String imageRelativeURL) {
        Image image = null;
        try {
            ClassLoader cl = ResourceManager.class.getClassLoader();
            image = Toolkit.getDefaultToolkit().getImage(cl.getResource(imageRelativeURL));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return image;
    }

    public static Properties loadProperties(String filename) throws java.io.IOException {
        return loadProperties(filename, ResourceManager.class);
    }

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
        return is;
    }

    public static Properties loadProperties(String filename, Class loadClass) throws java.io.IOException {
        InputStream is = loadClass.getClassLoader().getResourceAsStream(filename);
        return loadProperties(is);
    }

    public static Properties loadProperties(InputStream Istream) throws java.io.IOException {
        if (Istream == null) {
            System.err.println("Can't load properties file from the inputstream  --  NO resource with the specified name was found");
            throw new IOException(
                "Can't load properties file from the inputstream  --  NO resource with the specified name was found");
        }

        Properties props = null;

        synchronized (ResourceManager.class) {
            try {
                props = new Properties();
                props.load(Istream);
                Istream.close();
            } catch (IOException e) {
                System.err.println("Can't load properties for Input stream");
                e.printStackTrace();
                throw e;
            }
        }
        return props;
    }

    public static ImageIcon getImageIcon(String imageRelativeURL) {
        ImageIcon icon = null;
        try {

            ClassLoader cl = ResourceManager.class.getClassLoader();
            icon = new ImageIcon(cl.getResource(imageRelativeURL));
        } catch (Exception exp) {
            System.out.println("Could not load image " + imageRelativeURL);
            exp.printStackTrace();
        }
        return icon;
    }

}
