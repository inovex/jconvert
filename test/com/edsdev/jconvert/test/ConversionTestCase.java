package com.edsdev.jconvert.test;

import java.util.Iterator;

import junit.framework.TestCase;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.domain.DecimalConversion;
import com.edsdev.jconvert.domain.FractionalConversion;
import com.edsdev.jconvert.logic.ConversionGapBuilder;
import com.edsdev.jconvert.presentation.ConversionTypeData;

/**
 * @author Ed Sarrazin
 */
public class ConversionTestCase extends TestCase {
    public void testConversion() throws Exception {
        Conversion conversion = Conversion.createInstance("Celsius", "C", "Fahrenheit", "F", "9/5", 32);

        assertTrue("17 Celsius is 62.6 Fahrenheit", conversion.convertValue(17, conversion.getFromUnit()) == 62.6);
        assertTrue("62.6 Fahrenheit is 17.0 Celsius", conversion.convertValue(62.6, conversion.getToUnit()) == 17.0);
        assertTrue("62.6 Fahrenheit is 17.0 Celsius",
            conversion.convertValue(62.6, "Fahrenheit", "Celsius").doubleValue() == 17.0);
    }

    public void testWholeNumbers() throws Exception {
        Conversion c1 = Conversion.createInstance("a", "", "b", "", "345", 0);
        Conversion c2 = Conversion.createInstance("a", "", "c", "", "345.", 0);
        Conversion c3 = Conversion.createInstance("a", "", "d", "", "345.5", 0);
        Conversion c4 = Conversion.createInstance("a", "", "e", "", "345.567", 0);
        Conversion c5 = Conversion.createInstance("a", "", "f", "", "345.0006", 0);
        Conversion c6 = Conversion.createInstance("a", "", "g", "", "345.00", 0);

        assertTrue("a to b should be Fractional", c1 instanceof FractionalConversion);
        assertTrue("a to c should be Fractional", c2 instanceof FractionalConversion);
        assertTrue("a to d should be Decimal", c3 instanceof DecimalConversion);
        assertTrue("a to e should be Decimal", c4 instanceof DecimalConversion);
        assertTrue("a to f should be Decimal", c5 instanceof DecimalConversion);
        assertTrue("a to g should be Fractional", c6 instanceof FractionalConversion);

    }

    public void testFraction() throws Exception {
        Conversion c1 = Conversion.createInstance("a", "", "b", "", "1/3", 0);
        Conversion c2 = Conversion.createInstance("b", "", "c", "", "3/4", 0);
        Conversion c3 = Conversion.createInstance("c", "", "d", "", "2", 0);
        assertTrue("Is conversion a fraction", c1 instanceof FractionalConversion);

        ConversionType ct = new ConversionType();
        ct.setTypeName("Test Type");

        ct.addConversion(c1);
        ct.addConversion(c2);
        ct.addConversion(c3);

        ConversionGapBuilder.createOneToOneConversions(ct);
        ConversionGapBuilder.createMissingConversions(ct);
        ConversionTypeData ctd = new ConversionTypeData(ct);

        assertTrue("Verify count of FromUnits", ctd.getAllFromUnits().size() == 4);
        assertTrue("Verify count of to units from b", ctd.getToUnits("b").size() == 4);
        Iterator iter = ct.getConversions().iterator();
        boolean founda = false;
        boolean foundb = false;
        while (iter.hasNext()) {
            Conversion c = (Conversion) iter.next();
            assertTrue("Verify that calculated fraction is a fraction", c instanceof FractionalConversion);
            if (c.getFromUnit().equals("a") && c.getToUnit().equals("c")) {
                founda = true;
                FractionalConversion fc = (FractionalConversion) c;
                assertTrue("Verify that generated fraction is correct", fc.getFromToFactorString().equals("3/12"));
            } else if (c.getFromUnit().equals("a") && c.getToUnit().equals("d")) {
                foundb = true;
                FractionalConversion fc = (FractionalConversion) c;
                assertTrue("Verify that generated fraction is correct", fc.getFromToFactorString().equals("6/12"));
            }

        }
        assertTrue("Verify that fraction was created", founda);
        assertTrue("Verify that fraction was created", foundb);

		//assertions for testing fractional conversions using fractions and resulting in fractions
		FractionalConversion fc = (FractionalConversion)Conversion.createInstance("a", "", "b", "", "1/3", 2);
		System.out.println(fc.convertValue(1, 2, "a") + " should = " + fc.convertValue(0.5, "a"));
		System.out.println(fc.convertValue(1, 2, "b") + " should = " + fc.convertValue(0.5, "b"));
		assertTrue("1/2 converted to a should be 13/6", fc.convertValue(1, 2, "a").equals("13/6"));
		assertTrue("1/2 converted to b should be -9/2", fc.convertValue(1, 2, "b").equals("-9/2"));
    }
    


    public void testGapBuilder() throws Exception {
        Conversion c1 = Conversion.createInstance("minutes", "'", "hours", "hrs", "1/60", 0);
        Conversion c2 = Conversion.createInstance("hours", "hrs", "seconds", "''", "3600", 0);
        Conversion c3 = Conversion.createInstance("minutes", "'", "day", "", "1/1440", 0);
        Conversion c4 = Conversion.createInstance("year", "", "month", "", "12", 0);
        Conversion c5 = Conversion.createInstance("year", "", "month (30 days)", "", "73/6", 0);
        Conversion c6 = Conversion.createInstance("month (30 days)", "", "day", "", "30", 0);

        ConversionType ct = new ConversionType();
        ct.setTypeName("Time");

        ct.addConversion(c1);
        ct.addConversion(c2);
        ct.addConversion(c3);
        ct.addConversion(c4);
        ct.addConversion(c5);
        ct.addConversion(c6);

        ConversionGapBuilder.createOneToOneConversions(ct);
        ConversionGapBuilder.createMissingConversions(ct);
        Iterator iter = ct.getConversions().iterator();
        assertTrue(ct.getConversions().size() == 49);
        while (iter.hasNext()) {
            Conversion c = (Conversion) iter.next();
            System.out.println("[Age=" + c.getGenerationAge() + "] Convert 50 in " + c.getFromUnit() + " to "
                    + c.getToUnit() + " = " + c.convertValue(50.0, c.getFromUnit()));
        }

        ConversionTypeData ctd = new ConversionTypeData(ct);

        assertTrue("Verify count of FromUnits", ctd.getAllFromUnits().size() == 7);
        assertTrue("Verify count of to units from day", ctd.getToUnits("day").size() == 7);
        assertTrue("Verify day to year conversion", ctd.convert(50, "day", "year") == 0.136986301369863);
        assertTrue("Verify day to second conversion", ctd.convert(50, "day", "seconds") == 4320000.0);
        assertTrue("Verify minutes to seconds conversion", ctd.convert(50, "minutes", "seconds") == 3000.0);
        assertTrue("Verify day to hour conversion", ctd.convert(50, "day", "hours") == 1200.0);
        assertTrue("Verify year to month conversion", ctd.convert(50, "year", "month") == 600.0);
        assertTrue("Verify hours to seconds conversion", ctd.convert(50, "hours", "seconds") == 180000.0);

        // Celsius,Fahrenheit,1.8,32
        // Fahrenheit,Kelvin,0.5555555555555555555,255.3722222222222222222222222223
        // Celsius,Kelvin,2,273.15

    }
}
