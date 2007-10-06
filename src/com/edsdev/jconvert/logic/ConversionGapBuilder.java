package com.edsdev.jconvert.logic;

import java.util.HashMap;
import java.util.Iterator;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.presentation.ConversionTypeData;
import com.edsdev.jconvert.util.Logger;

public class ConversionGapBuilder {

    private static Logger log = Logger.getInstance(ConversionGapBuilder.class);

    private static HashMap ageMap;

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
            log.debug("[Age=" + c.getGenerationAge() + "] Convert 50 in " + c.getFromUnit() + " to " + c.getToUnit()
                    + " = " + c.convertValue(50.0, c.getFromUnit()));
        }

        ConversionTypeData ctd = new ConversionTypeData();
        ctd.setType(ct);
        log.debug(ctd.getAllFromUnits());
        log.debug(ctd.getToUnits("day"));
        log.debug(ctd.convert(50, "day", "year") + "");
        log.debug(ctd.convert(50, "day", "seconds") + "");

        // Celsius,Fahrenheit,1.8,32
        // Fahrenheit,Kelvin,0.5555555555555555555,255.3722222222222222222222222223
        // Celsius,Kelvin,2,273.15
    }

    /**
     * This method is responsible for creating the one-to-one conversions. All these are are the conversions that
     * convert the same unit of measure to itself. These are boring calculations that no-one really wants to enter in
     * their conversion tables. But it makes the GUI nice when selecting around and the To lists do not keep shifting
     * around
     * 
     * @param ct ConversionType that you want to create the one-to-one conversions in.
     */
    public static void createOneToOneConversions(ConversionType ct) {
        if (ct.getConversions().size() <= 1) {
            return;
        }
        Object[] list = ct.getConversions().toArray();
        for (int i = 0; i < list.length; i++) {
            Conversion conv = (Conversion) list[i];
            Conversion newC = Conversion.createInstance(conv.getFromUnit(), conv.getFromUnitAbbr(), conv.getFromUnit(),
                conv.getFromUnitAbbr(), "1", 0);
            if (!ct.getConversions().contains(newC)) {
                ct.addConversion(newC);
            }
            newC = Conversion.createInstance(conv.getToUnit(), conv.getToUnitAbbr(), conv.getToUnit(),
                conv.getToUnitAbbr(), "1", 0);
            if (!ct.getConversions().contains(newC)) {
                ct.addConversion(newC);
            }
        }

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
        ageMap = new HashMap();
        boolean added = true;
        while (added) {
            added = false;
            Object[] outerArray = ct.getConversions().toArray();
            for (int i = 0; i < outerArray.length; i++) {
                Conversion outer = (Conversion) outerArray[i];
                Object[] innerArray = ct.getConversions().toArray();
                for (int j = 0; j < innerArray.length; j++) {
                    Conversion inner = (Conversion) innerArray[j];
                    if (outer.getFromToOffset() == 0 && inner.getFromToOffset() == 0) {
                        if (outer.getToUnit().equals(inner.getFromUnit())) {
                            String newFactor = outer.multiply(inner);
                            if (checkAdd(ct, outer.getFromUnit(), outer.getFromUnitAbbr(), inner.getToUnit(),
                                inner.getToUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
                                added = true;
                            }
                        } else if (outer.getToUnit().equals(inner.getToUnit())) {
                            String newFactor = outer.divide(inner);
                            if (checkAdd(ct, outer.getFromUnit(), outer.getFromUnitAbbr(), inner.getFromUnit(),
                                inner.getFromUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
                                added = true;
                            }
                        } else if (outer.getFromUnit().equals(inner.getFromUnit())) {
                            String newFactor = inner.divide(outer);
                            if (checkAdd(ct, outer.getToUnit(), outer.getToUnitAbbr(), inner.getToUnit(),
                                inner.getToUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
                                added = true;
                            }
                        }
                    }
                }
            }
        }
        ageMap = null;
    }

    private static boolean checkAdd(ConversionType ct, String from, String fromAbbr, String to, String toAbbr,
            int fromAge, int toAge, String newFactor) {
        //TODO - get a generation age for this new conversion. Add a helper method to compare which conversion
        //is younger and use that instead of the if below. will also need to use this on all ifs below.

        //below I have used the hashcode for the Conversion. Unfortunately, the hashcode
        //was designed to handle... get back to this comment
        Conversion newC = Conversion.createEmptyInstance(from, fromAbbr, to, toAbbr);
        Object lastAge = ageMap.get(newC.getFromUnit() + newC.getToUnit());
        if (!ct.getConversions().contains(newC) || lastAge != null) {
            //      if (!ct.getConversions().contains(newC)) {
            newC = Conversion.createInstance(from, fromAbbr, to, toAbbr, newFactor, 0);
            setFactorToOneIfUnitsEqual(newC);
            newC.setGenerationAge(fromAge + toAge + 1);
            if (lastAge == null || ((Integer) lastAge).intValue() > newC.getGenerationAge()) {
                ct.getConversions().remove(newC);
                ct.addConversion(newC);
                ageMap.put(newC.getFromUnit() + newC.getToUnit(), new Integer(newC.getGenerationAge()));
                return true;
            }
        }
        return false;

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
