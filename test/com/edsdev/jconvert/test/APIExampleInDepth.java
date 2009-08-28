package com.edsdev.jconvert.test;

import java.util.Iterator;
import java.util.List;

import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.presentation.ConversionTypeData;

public class APIExampleInDepth {

	private static List domainData = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		domainData = new DataLoader().loadData();
		printOutAllTypes();

		// assume non null returns for these tests - makes examples cleaner
		ConversionTypeData ctd = getConversionType("Distance");
		System.out.println("1 mile = " + ctd.convert(1.0, "mile", "meter") + " meters");
		System.out.println("4000 meters = " + ctd.convertFraction("4000", "meter", "mile") + " miles");
		System.out.println("4000 1/2 meters = " + ctd.convertFraction("4000 1/2", "meter", "mile") + " miles");

		// This section will assume (for these testing purposes, that you have added a convert_custom.dat
		// It also demonstrates how you can get "free" conversions that you did not enter, Notice that in
		// the test data below we did not enter conversions for Billy to anyone else, yet JConvert will calculate
		// file with the following entries (or more)
		// TestType,Ed,,Billy,,2,0
		// TestType,Ed,,Sarah,,4,0
		// TestType,Ed,,Tom,,1/2,0
		ctd = getConversionType("TestType");
		System.out.println("1 Sarah = " + ctd.convert(1, "Sarah", "Tom") + " Tom");
		System.out.println("4000 Billy = " + ctd.convertFraction("4000", "Billy", "Sarah") + " Sarah");
		System.out.println("4000 1/2 Billy = " + ctd.convertFraction("4000 1/2", "Billy", "Sarah") + " Sarah");
	}

	/**
	 * @param conversionType String representation of the conversion type you seek
	 * @return ConversionTypeData element designed to allow access to conversions
	 */

	private static ConversionTypeData getConversionType(String conversionType) {
		ConversionTypeData ctd = null;
		Iterator iter = domainData.iterator();
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			if (type.getTypeName().equals(conversionType)) {
				ctd = new ConversionTypeData(type);
				break;
			}
		}

		return ctd;
	}

	private static void printOutAllTypes() {
		Iterator iter = domainData.iterator();
		System.out.println("-- ALL Conversion Types ------");
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			System.out.println(type.getTypeName() + " - " + type.getConversions().size() + " conversions");

		}
		System.out.println("------------------------------");

	}

}
