package com.edsdev.jconvert.persistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.logic.ConversionGapBuilder;
import com.edsdev.jconvert.util.ResourceManager;

/**
 * This class is responsible for creating ConversionTypes by loading data from the data files that contain the
 * specifiactions. <br>
 * <br>
 * Here is an example of the datafile format:
 * <li>#Conversion Type Name, FromUnit, From Unit Abbr, To Unit, To Unit Abbr, factor, offset
 * <li>Temperature,Celsius,ºC,Celsius,ºC,1,0
 * <li>Temperature,Celsius,ºC,Fahrenheit,ºF,9/5,32
 * <li>Time,century,,day,dy,36500,0
 * <li>Time,century,,decade,,10,0
 * <li>Time,century,,fortnight,,18250/7,0
 * <li>Time,century,,hour,hr,876000,0
 * <li>Time,century,,leap-year,,36500/366,0
 * <li>Time,century,,millennium,,0.10,0
 * 
 * @author Ed Sarrazin Created on Jul 17, 2007 6:59:31 PM
 */
public class DataLoader {

    private boolean generateGaps = true;

    public static void main(String[] args) {
        StringTokenizer stoken = new StringTokenizer("test1,test2,,test4".replaceAll(",", ", "), ",");
        while (stoken.hasMoreTokens()) {
            System.out.println(stoken.nextToken());
        }

    }

    /**
     * Loads the data from the input stream into the HashMap of ConversionTypes. If the conversion type does not exist
     * in the map, one will be created. This allows you to call this method multiple times and load data from more than
     * one file.
     * 
     * @param stream InputStream that is pointing to a datafile to load.
     * @param map HashMap of ConversionTypes. Can start out empty or have data.
     */
    private void loadDataFromStream(InputStream stream, HashMap map) {
        if (stream == null) {
            return;
        }
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(stream));
            String line = null;

            while ((line = input.readLine()) != null) {
                StringTokenizer stok = new StringTokenizer(line.replaceAll(",", ", "), ",");
                if (!line.startsWith("#") && stok.countTokens() == 7) {
                    String tab = stok.nextToken().trim();
                    String fromUnit = stok.nextToken().trim();
                    String fromUnitAbbr = stok.nextToken().trim();
                    String toUnit = stok.nextToken().trim();
                    String toUnitAbbr = stok.nextToken().trim();
                    String factor = stok.nextToken().trim();
                    String offset = stok.nextToken().trim();

                    Conversion conversion = Conversion.createInstance(fromUnit, fromUnitAbbr, toUnit, toUnitAbbr,
                        factor, Double.parseDouble(offset));
                    ConversionType ct = getConversionType(map, tab);
                    ct.addConversion(conversion);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    // flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * This method will load data from convert.dat and convert_custom.dat. It will look in the classpath for these
     * files.
     * 
     * @return List of ConversionTypes
     */
    public List loadData() {
        // return getHardCodedData();
        HashMap map = new HashMap();
        Date date1 = new Date();

        loadDataFromStream(ResourceManager.getResourceAsStream("convert.dat"), map);
        loadDataFromStream(ResourceManager.getResourceAsStream("convert_custom.dat"), map);

        Date date2 = new Date();
        System.out.println("Loaded file - " + (date2.getTime() - date1.getTime()) + " miliseconds");
        List rv = new ArrayList(map.values());

        if (!generateGaps) {
            return rv;
        }

        Iterator iter = rv.iterator();
        while (iter.hasNext()) {
            ConversionType ct = (ConversionType) iter.next();
            ConversionGapBuilder.createMissingConversions(ct);
        }

        date1 = new Date();
        System.out.println("Created conversions - " + (date1.getTime() - date2.getTime()) + " miliseconds");
        return rv;
    }

    private ConversionType getConversionType(HashMap map, String type) {
        if (map.containsKey(type)) {
            return (ConversionType) map.get(type);
        }
        ConversionType ct = new ConversionType();
        ct.setTypeName(type);
        map.put(type, ct);
        return ct;
    }

    private List getHardCodedData() {
        List rv = new ArrayList();

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

        ConversionGapBuilder.createMissingConversions(ct);

        rv.add(ct);

        c1 = Conversion.createInstance("C", "", "miles/hour", "mph", "670616629.4", 0);
        c2 = Conversion.createInstance("C", "", "feet/second", "fps", "983571056.4", 0);
        c3 = Conversion.createInstance("C", "", "meters/second", "", "299792458", 0);
        c4 = Conversion.createInstance("C", "", "knot", "", "582749918.4", 0);
        c5 = Conversion.createInstance("C", "", "mach", "", "904460.4418", 0);
        c6 = Conversion.createInstance("C", "", "kilometer/hour", "kph", "1079252849", 0);

        ct = new ConversionType();
        ct.setTypeName("Speed");

        ct.addConversion(c1);
        ct.addConversion(c2);
        ct.addConversion(c3);
        ct.addConversion(c4);
        ct.addConversion(c5);
        ct.addConversion(c6);

        ConversionGapBuilder.createMissingConversions(ct);

        rv.add(ct);

        return rv;
    }
}
