package com.edsdev.jconvert.test;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.edsdev.jconvert.util.Messages;

/**
 * @author Ed Sarrazin
 */
public class InternationalizeTestCase extends TestCase {
    public void testFrench() throws Exception {
        Locale locale = new Locale("fr");
        languageTest(locale);
    }

    public void testSpanish() throws Exception {
        Locale locale = new Locale("es");
        languageTest(locale);
    }

    public void testRussian() throws Exception {
        Locale locale = new Locale("ru");
        languageTest(locale);
    }

    private void languageTest(Locale locale) {
        //initialize locale to something that should never happen, that way you get the default properties file
        Locale.setDefault(new Locale("xxx"));
        ResourceBundle bundle = ResourceBundle.getBundle("jcMessages");
        assertNotNull("Could not locate the resource bundle.", bundle);
        Enumeration enumeration = bundle.getKeys();

        //point the bundle at the locale passed in.
        Locale.setDefault(locale);
        Messages.resetBundle();

        while (enumeration.hasMoreElements()) {
            Object key = enumeration.nextElement();
            try {
                String value = Messages.getResource(key.toString());
                assertNotNull("Value for key " + key + " should be non-null", value);
            } catch (Exception e) {
                assertTrue("Could not find key " + key + " for locale " + locale, false);
            }
        }
    }

}
