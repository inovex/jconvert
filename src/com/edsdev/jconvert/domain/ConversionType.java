package com.edsdev.jconvert.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.edsdev.jconvert.util.Logger;

/**
 * Simple domain object that facilitates segregation of like conversions. For example, there will be a conversion type
 * for time that will contain many conversions, like minutes to seconds and hours to weeks.
 * 
 * @author Ed Sarrazin Created on Jul 14, 2007 10:06:32 AM
 */
public class ConversionType {
    private static Logger log = Logger.getInstance(ConversionType.class);
    
    private String typeName;

    private Collection conversions = new HashSet();

    public void addConversion(Conversion conversion) {
        conversions.add(conversion);
    }

    public Collection getConversions() {
        return conversions;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void printDetails() {
        Iterator iter = conversions.iterator();
        while (iter.hasNext()) {
            Conversion conv = (Conversion) iter.next();
            log.debug(typeName + ": " + conv.getFromUnit() + " TO " + conv.getToUnit() + " : "
                    + conv.getFromToFactor() + " - " + conv.getGenerationAge());
        }
    }
    public boolean equals(Object obj) {
        if (obj instanceof ConversionType) {
            if (((ConversionType)obj).getTypeName().equals(this.getTypeName())) {
                return true;
            }
        }
        return false;
    }

}
