package com.edsdev.jconvert.test;

import java.util.Iterator;
import java.util.List;

import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.presentation.ConversionTypeData;

public class APIExampleSimple {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        List domainData = new DataLoader().loadData();
        ConversionTypeData ctd = null;
        Iterator iter = domainData.iterator();
        while (iter.hasNext()) {
            ConversionType type = (ConversionType) iter.next();
            if (type.getTypeName().equals("Distance")) {
                ctd = new ConversionTypeData(type);
                break;
            }
        }
        
        if (ctd != null) {
        	System.out.println("1 mile = " + ctd.convert(1.0, "mile", "meter") + " meters"); 
        	System.out.println("1 mile = " + ctd.convertFraction("1", "mile", "meter") + " meters"); 
        }

	}
	
	private ConversionTypeData getConversionType(String conversionType) {
		return null;
	}

}
