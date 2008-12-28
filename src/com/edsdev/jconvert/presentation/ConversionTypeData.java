package com.edsdev.jconvert.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;

/**
 * Presentation side representation of Conversion Data
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 8:02:09 PM
 */
public class ConversionTypeData implements Comparable {
    private ConversionType type;

    public ConversionTypeData(ConversionType ct) {
        super();
        this.setType(ct);
    }

    public String getTypeName() {
        return type.getTypeName();
    }

    public List getAllFromUnits() {
        HashSet set = new HashSet();
        Iterator iter = type.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            set.add(new ConversionUnitData(conv.getFromUnit(), conv.getFromUnitAbbr(), 0));
            set.add(new ConversionUnitData(conv.getToUnit(), conv.getToUnitAbbr(), 0));
        }
        List rv = new ArrayList();
        rv.addAll(set);
        Collections.sort(rv);
        return rv;
    }

    public List getToUnits(String fromUnit) {
        HashSet set = new HashSet();
        Iterator iter = type.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            String temp = conv.getConversionPartner(fromUnit);
            if (temp != null) {
                String temp2 = conv.getConversionPartnerAbbrev(fromUnit);
                set.add(new ConversionUnitData(temp, temp2, conv.getGenerationAge()));
            }
        }
        List rv = new ArrayList();
        rv.addAll(set);
        Collections.sort(rv);
        return rv;
    }

    public double convert(double startValue, String fromUnit, String toUnit) {
        float generation = 10000000;
        double finalValue = 0;

        Iterator iter = type.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            Double temp = conv.convertValue(startValue, fromUnit, toUnit);
            if (temp != null) {
                float tempGen = conv.getGenerationAge();
                if (conv.getFromUnit().equals(fromUnit)) {
                    tempGen = tempGen - 0.5f; // favor conversions that do not
                    // have to be inverted
                }
                if (generation > tempGen) {
                    finalValue = temp.doubleValue();
                    generation = tempGen;
                }
            }
        }
        return finalValue;
    }

    public double convert(String startValue, String fromUnit, String toUnit) {
        float generation = 10000000;
        double finalValue = 0;

        startValue = cleanStartValue(startValue);

        Iterator iter = type.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            Double temp = conv.convertValue(startValue, fromUnit, toUnit);
            if (temp != null) {
                float tempGen = conv.getGenerationAge();
                if (conv.getFromUnit().equals(fromUnit)) {
                    tempGen = tempGen - 0.5f; // favor conversions that do not
                    // have to be inverted
                }
                if (generation > tempGen) {
                    finalValue = temp.doubleValue();
                    generation = tempGen;
                }
            }
        }
        return finalValue;
    }

    /**
     * This method simply cleans starting values like "12 1" when someone is starting to type a fraction, but has not
     * yet completed it. This method will return 12 since 12 1 makes no sense.
     * 
     * @param startValue
     * @return
     */
    private String cleanStartValue(String startValue) {
        int spacePos = startValue.indexOf(" ");
        int slashPos = startValue.indexOf("/");
        if (slashPos > 0) {
            return startValue;
        }

        if (spacePos < 0) {
            return startValue;
        }

        return startValue.substring(0, spacePos);
    }

    public String convertFraction(String startValue, String fromUnit, String toUnit) {
        String rv = "";

        startValue = cleanStartValue(startValue);
        Iterator iter = type.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            rv = conv.convertFraction(startValue, fromUnit, toUnit);
            if (rv != null) {
                return rv;
            }
        }
        return rv;
    }

    public ConversionType getType() {
        return type;
    }

    public void setType(ConversionType type) {
        this.type = type;
    }

    public int compareTo(Object o) {
        if (o instanceof ConversionTypeData) {
            return type.getTypeName().compareTo(((ConversionTypeData) o).getType().getTypeName());
        }
        return 0;
    }

}
