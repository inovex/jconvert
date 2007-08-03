package com.edsdev.jconvert.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.domain.FractionalConversion;
import com.edsdev.jconvert.logic.ConversionGapBuilder;
import com.edsdev.jconvert.util.Logger;
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
    private static Logger log = Logger.getInstance(DataLoader.class);

    public static void main(String[] args) {
        StringTokenizer stoken = new StringTokenizer("test1,test2,,test4".replaceAll(",", ", "), ",");
        while (stoken.hasMoreTokens()) {
            log.debug(stoken.nextToken());
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
            log.error("Could not load data from stream", ex);
        } catch (IOException ex) {
            log.error("Could not load data from stream", ex);
        } catch (Exception ex) {
            log.error("Could not load data from stream", ex);
        } finally {
            try {
                if (input != null) {
                    // flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                log.error("Could not load data from stream", ex);
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
        //load each of the two files
        List main = loadData("convert.dat", false, null);
        List addOns = loadData("convert_custom.dat", true, main);

        //merge the data between the two.
        List rv = new ArrayList();
        rv.addAll(main);
        Iterator iter = addOns.iterator();
        while (iter.hasNext()) {
            ConversionType type = (ConversionType) iter.next();
            if (rv.contains(type)) {
                for (int i = 0; i < rv.size(); i++) {
                    ConversionType existing = (ConversionType) rv.get(i);
                    if (existing.equals(type)) {
                        mergeConversionType(existing, type);
                        break; //get out of for loop, we are done.
                    }
                }
            } else {
                rv.add(type);
            }
        }

        return rv;
    }

    /**
     * This method is responsible for merging the ConversionType data into the existing ConversionType data. It uses
     * some logic to determine if the new data is non-existent in the existing data. If so it adds it. If it already
     * exists in the existing data, then it compares the two Conversions and determines which conversion to use. This is
     * simple logic looking at the generation age of the data.
     * 
     * @param dest ConversionType data that we want to merge into. This is considered existing data
     * @param newData ConversionType data that we want to merge into the destination.
     */
    private void mergeConversionType(ConversionType dest, ConversionType newData) {
        Iterator iter = newData.getConversions().iterator();
        while (iter.hasNext()) {
            Conversion newConversion = (Conversion) iter.next();
            if (dest.getConversions().contains(newConversion)) {
                Iterator iter2 = dest.getConversions().iterator();
                while (iter2.hasNext()) {
                    Conversion destConversion = (Conversion) iter2.next();
                    if (destConversion.equals(newConversion)) {
                        if (destConversion.getGenerationAge() >= newConversion.getGenerationAge()) {
                            dest.getConversions().remove(destConversion);
                            dest.getConversions().add(newConversion);
                        }
                        break; //shortcut out of while
                    }
                }
            } else {
                dest.getConversions().add(newConversion);
            }
        }
    }

    /**
     * This method is responsible for loading data from a resource into a list. existing conversion types are passed
     * into this method to provide support for generating gaps. For example if you create a conversion of decades to
     * doubledecades and that is it. We will look at the existing conversions to also generate gaps such as
     * doubledecades to year, months, days, etc.
     * 
     * @param resourceName String name of the resource that has the data to be loaded
     * @param generateGaps boolean that indicates whether or not to spend time generating missing conversions. This is
     *        needed because generating these gaps takes significant time and the default conversions that are supplied
     *        will have all combinatorics created before hand
     * @param existingConversionTypes List of existing conversion types that will be used in aiding generation of
     *        missing conversion types.
     * @return List of ConversionTypes.
     */
    private List loadData(String resourceName, boolean generateGaps, List existingConversionTypes) {
        HashMap map = new HashMap();
        Date date1 = new Date();

        loadDataFromStream(ResourceManager.getResourceAsStream(resourceName), map);

        Date date2 = new Date();
        log.debug("Loaded file - " + (date2.getTime() - date1.getTime()) + " miliseconds");
        List rv = new ArrayList(map.values());

        Iterator iter = rv.iterator();
        while (iter.hasNext()) {
            ConversionType ct = (ConversionType) iter.next();
            ConversionGapBuilder.createOneToOneConversions(ct);
        }

        if (!generateGaps) {
            return rv;
        }

        iter = rv.iterator();
        while (iter.hasNext()) {
            ConversionType ct = (ConversionType) iter.next();
            Iterator existingIter = existingConversionTypes.iterator();
            while (existingIter.hasNext()) {
                ConversionType existingCt = (ConversionType) existingIter.next();
                if (ct.getTypeName().equals(existingCt.getTypeName())) {
                    ct.getConversions().addAll(existingCt.getConversions());
                    break;
                }
            }
            ConversionGapBuilder.createMissingConversions(ct);
        }

        date1 = new Date();
        log.debug("Created conversions - " + (date1.getTime() - date2.getTime()) + " miliseconds");
        //        this.unloadData(rv);
        return rv;
    }

    /**
     * Simply looks in the map for the conversionType (indicated by the string) and returns it. If it is not found, then
     * it creates it, puts it in the HashMap and returns it to you.
     * 
     * @param map HashMap of existing ConversionTypes
     * @param type String name of the conversion type
     * @return ConversionType
     */
    private ConversionType getConversionType(HashMap map, String type) {
        if (map.containsKey(type)) {
            return (ConversionType) map.get(type);
        }
        ConversionType ct = new ConversionType();
        ct.setTypeName(type);
        map.put(type, ct);
        return ct;
    }

    /**
     * Writes the List of ConversionTypes to output.dat.
     * 
     * @param domainData List of ConversionTypes
     */
    public void unloadData(List domainData) {
        //iterate through conversiontypes and write out.
        Iterator iter = domainData.iterator();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.dat"));
            while (iter.hasNext()) {
                ConversionType ct = (ConversionType) iter.next();
                List lst = new ArrayList();
                lst.addAll(ct.getConversions());
                Collections.sort(lst);
                Iterator iter2 = lst.iterator();
                while (iter2.hasNext()) {
                    Conversion c = (Conversion) iter2.next();
                    if (c instanceof FractionalConversion) {
                        writer.write(ct.getTypeName() + "," + c.getFromUnit() + "," + c.getFromUnitAbbr() + ","
                                + c.getToUnit() + "," + c.getToUnitAbbr() + "," + c.getFromToTopFactor() + "/"
                                + c.getFromToBottomFactor() + "," + c.getFromToOffset() + "\n");
                    } else {
                        writer.write(ct.getTypeName() + "," + c.getFromUnit() + "," + c.getFromUnitAbbr() + ","
                                + c.getToUnit() + "," + c.getToUnitAbbr() + "," + c.getFromToFactor() + ","
                                + c.getFromToOffset() + "\n");
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
