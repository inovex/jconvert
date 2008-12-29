package com.edsdev.jconvert.logic;

import java.util.HashMap;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.domain.FractionalConversion;

/**
 * This class is responsible for generating missing conversions in a ConversionType list. The name is a bit misleading.
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 7:54:38 PM
 */
public class ConversionGapBuilder {

    private static HashMap ageMap;

    /**
     * This method is responsible for creating the one-to-one conversions. All these are are the conversions that
     * convert the same unit of measure to itself. These are boring calculations that no-one really wants to enter in
     * their conversion tables. But it makes the GUI nice when selecting around and the To lists do not keep shifting
     * around
     * 
     * @param ct ConversionType that you want to create the one-to-one conversions in.
     */
    public static void createOneToOneConversions(ConversionType ct) {
        Object[] list = ct.getConversions().toArray();
        for (int i = 0; i < list.length; i++) {
            Conversion conv = (Conversion) list[i];
            Conversion newC = Conversion.createInstance(conv.getFromUnit(), conv.getFromUnitAbbr(), conv.getFromUnit(),
                conv.getFromUnitAbbr(), "1", 0);
            if (!ct.getConversions().contains(newC)) {
                ct.addConversion(newC);
            }
            newC = Conversion.createInstance(conv.getToUnit(), conv.getToUnitAbbr(), conv.getToUnit(), conv
                .getToUnitAbbr(), "1", 0);
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
                            if (checkAdd(ct, outer.getFromUnit(), outer.getFromUnitAbbr(), inner.getToUnit(), inner
                                .getToUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
                                added = true;
                            }
                        } else if (outer.getToUnit().equals(inner.getToUnit())) {
                            String newFactor = outer.divide(inner);
                            if (checkAdd(ct, outer.getFromUnit(), outer.getFromUnitAbbr(), inner.getFromUnit(), inner
                                .getFromUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
                                added = true;
                            }
                        } else if (outer.getFromUnit().equals(inner.getFromUnit())) {
                            String newFactor = inner.divide(outer);
                            if (checkAdd(ct, outer.getToUnit(), outer.getToUnitAbbr(), inner.getToUnit(), inner
                                .getToUnitAbbr(), outer.getGenerationAge(), inner.getGenerationAge(), newFactor)) {
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
        
        //create dummy instance to check against age map and collection of conversions
        Conversion newC = Conversion.createEmptyInstance(from, fromAbbr, to, toAbbr);
        Object lastAge = ageMap.get(newC.getFromUnit() + newC.getToUnit());
        if (!ct.getConversions().contains(newC) || lastAge != null) {
            //if we need to add this conversion, then let's really create it now.
            newC = Conversion.createInstance(from, fromAbbr, to, toAbbr, newFactor, 0);
            setFactorToOneIfUnitsEqual(newC);
            // Fractional conversions have no loss of precision
            if (!(newC instanceof FractionalConversion)) {
                newC.setGenerationAge(fromAge + toAge + 1);
            }
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
     * Convenience method if you create a conversion that converts the same unit to itself, sometimes there are
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
