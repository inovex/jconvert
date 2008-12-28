package com.edsdev.jconvert.test;

import junit.framework.TestCase;

import com.edsdev.jconvert.domain.FractionalConversion;

/**
 * @author Ed Sarrazin
 */
public class FractionTestCase extends TestCase {
    public void testReduction() throws Exception {
        assertTrue("34345454/16 should be 2146590 7/8", FractionalConversion.reduceFraction("34345454/16").equals(
            "2146590 7/8"));
        assertTrue("2/50 should be 1/25", FractionalConversion.reduceFraction("2/50").equals("1/25"));

        assertTrue("1/2 should = 1/2", FractionalConversion.reduceFraction("1/2").equals("1/2"));
        assertTrue("3/2 should = 1 1/2", FractionalConversion.reduceFraction("3/2").equals("1 1/2"));
        assertTrue("4/2 should = 2", FractionalConversion.reduceFraction("4/2").equals("2"));
        assertTrue("5/2 should = 2 1/2", FractionalConversion.reduceFraction("5/2").equals("2 1/2"));

        // Fractional conversion will never be trying to reduce decimals - at least for now.
        // assertTrue("0.345 should = 0.345", FractionalConversion.reduceFraction("0.345").equals("0.345"));
        // assertTrue("0.3/45 should = 0.3/45", FractionalConversion.reduceFraction("0.3/45").equals("0.3/45"));

    }

}
