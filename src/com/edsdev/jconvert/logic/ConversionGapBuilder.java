package com.edsdev.jconvert.logic;

import java.util.Iterator;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.presentation.ConversionTypeData;

public class ConversionGapBuilder {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Conversion c1 = Conversion.createInstance("minutes", "'", "hours", "hrs",
            "0.016666666666666666666666666666667", 0);
        Conversion c2 = Conversion.createInstance("hours", "hrs", "seconds", "''", "3600", 0);
        Conversion c3 = Conversion.createInstance("minutes", "'", "day", "", "0.00069444444444444444444444444444444", 0);
        Conversion c4 = Conversion.createInstance("year", "", "month", "", "12", 0);
        Conversion c5 = Conversion.createInstance("year", "", "month (30 days)", "", "12.16666667", 0);
        Conversion c6 = Conversion.createInstance("month (30 days)", "", "day", "", "30", 0);

        ConversionType ct = new ConversionType();
        ct.setTypeName("Time");

        ct.addConversion(c1);
        ct.addConversion(c2);
        ct.addConversion(c3);
        ct.addConversion(c4);
        ct.addConversion(c5);
        ct.addConversion(c6);

        createMissingConversions(ct);
        Iterator iter = ct.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion c = (Conversion) iter.next();
            System.out.println("[Age=" + c.getGenerationAge() + "] Convert 50 in " + c.getFromUnit() + " to "
                    + c.getToUnit() + " = " + c.convertValue(50.0, c.getFromUnit()));
        }

        ConversionTypeData ctd = new ConversionTypeData();
        ctd.setType(ct);
        System.out.println(ctd.getAllFromUnits());
        System.out.println(ctd.getToUnits("day"));
        System.out.println(ctd.convert(50, "day", "year"));
        System.out.println(ctd.convert(50, "day", "seconds"));

        // Celsius,Fahrenheit,1.8,32
        // Fahrenheit,Kelvin,0.5555555555555555555,255.3722222222222222222222222223
        // Celsius,Kelvin,2,273.15
    }

    /**
     * Looks through all of the conversions in the specified ConversionType and tries to create missing conversions
     * through association. For example, if you have a conversion from feet to yards and from miles to yards, this
     * method should create the conversions feet to feet, feet to miles, yards to feet, yards to miles, yards to yards,
     * miles to miles, and miles to feet. Note that due to the math involved, if there is an offset in the current
     * conversion, then there will not be any other calculated conversions from that conversion
     * 
     * @param ct ConversionType
     */
    public static void createMissingConversions(ConversionType ct) {
        // brute force method first - other idea is a tree - fast enough right now
        // if there is an offset, no conversions will be calculated
        if (ct.getConversions().size() <= 1) {
            return;
        }
        boolean added = true;
        while (added) {
            added = false;
            Object[] outerArray = ct.getConversions().toArray();
            for (int i = 0; i < outerArray.length; i++) {
                Conversion outer = (Conversion) outerArray[i];
                Object[] innerArray = ct.getConversions().toArray();
                for (int j = 0; j < innerArray.length; j++) {
                    Conversion inner = (Conversion) innerArray[j];
                    if (outer.getToUnit().equals(inner.getFromUnit())) {
                        if (outer.getFromToOffset() == 0 && inner.getFromToOffset() == 0) {
                            String newFactor = outer.multiply(inner);
                            Conversion newC = Conversion.createEmptyInstance(outer.getFromUnit(),
                                outer.getFromUnitAbbr(), inner.getToUnit(), inner.getToUnitAbbr());
                            if (!ct.getConversions().contains(newC)) {
                                newC = Conversion.createInstance(outer.getFromUnit(), outer.getFromUnitAbbr(),
                                    inner.getToUnit(), inner.getToUnitAbbr(), newFactor, 0);
                                setFactorToOneIfUnitsEqual(newC);
                                newC.setGenerationAge(outer.getGenerationAge() + inner.getGenerationAge() + 1);
                                ct.addConversion(newC);
                                added = true;
                                // System.out.println(newC);
                            }
                        }
                    } else if (outer.getToUnit().equals(inner.getToUnit())) {
                        if (outer.getFromToOffset() == 0 && inner.getFromToOffset() == 0) {
                            String newFactor = outer.divide(inner);
                            Conversion newC = Conversion.createEmptyInstance(outer.getFromUnit(),
                                outer.getFromUnitAbbr(), inner.getToUnit(), inner.getToUnitAbbr());
                            if (!ct.getConversions().contains(newC)) {
                                newC = Conversion.createInstance(outer.getFromUnit(), outer.getFromUnitAbbr(),
                                    inner.getToUnit(), inner.getToUnitAbbr(), newFactor, 0);
                                setFactorToOneIfUnitsEqual(newC);
                                newC.setGenerationAge(outer.getGenerationAge() + inner.getGenerationAge() + 1);
                                ct.addConversion(newC);
                                added = true;
                                // System.out.println(newC);
                            }
                        }
                    } else if (outer.getFromUnit().equals(inner.getFromUnit())) {
                        if (outer.getFromToOffset() == 0 && inner.getFromToOffset() == 0) {
                            String newFactor = inner.divide(outer);
                            Conversion newC = Conversion.createEmptyInstance(outer.getToUnit(), outer.getToUnitAbbr(),
                                inner.getToUnit(), inner.getToUnitAbbr());
                            if (!ct.getConversions().contains(newC)) {
                                newC = Conversion.createInstance(outer.getToUnit(), outer.getToUnitAbbr(),
                                    inner.getToUnit(), inner.getToUnitAbbr(), newFactor, 0);
                                setFactorToOneIfUnitsEqual(newC);
                                newC.setGenerationAge(outer.getGenerationAge() + inner.getGenerationAge() + 1);
                                ct.addConversion(newC);
                                added = true;
                                // System.out.println(newC);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Convienience method if you create a conversion that converts the same unit to itself, sometimes there are
     * precision issues and it is not exactly one, this methods fixes that for you.
     * 
     * @param conversion
     */
    private static void setFactorToOneIfUnitsEqual(Conversion conversion) {
        if (conversion.getFromUnit().equals(conversion.getToUnit())) {
            conversion.setFromToFactorString("1");
        }
    }

}
