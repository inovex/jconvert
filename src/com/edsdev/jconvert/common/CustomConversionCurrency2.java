package com.edsdev.jconvert.common;

import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.logic.ConversionGapBuilder;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.util.Http;

/**
 * This class implements the CustomConversionDataInterface and is responsible for gathering custom conversions. In
 * particular, this class will download the latest currency conversions from an external website. Use this class as an
 * example if you want to create your own interface to the application.
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 4:00:57 PM
 */
public class CustomConversionCurrency2 extends CustomConversionImpl implements CustomConversionDataInterface {

    private static final String theUrl = "http://finance.yahoo.com/currency?u";

    private static final String TAB_NAME = "Currency";

    /*
     * (non-Javadoc)
     * 
     * @see com.edsdev.jconvert.common.CustomConversionDataInterface#getConversions()
     */
    public ConversionType getConversions() {
        ConversionType rv = new ConversionType();
        rv.setTypeName(TAB_NAME);
        updateData(rv);
        ConversionGapBuilder.createOneToOneConversions(rv);
        ConversionGapBuilder.createMissingConversions(rv);

        DataLoader dataLoader = new DataLoader();
        ArrayList list = new ArrayList();
        list.add(rv);
        dataLoader.unloadData(list, "YahooCurrency.dat");
        return rv;
    }

    private void updateData(ConversionType ct) {
        StringBuffer loadedUpdate = null;
        try {
            URLConnection uc = Http.getPage(theUrl);
            loadedUpdate = Http.receivePage(uc, "UTF-8");
        } catch (IOException e) {
            loadedUpdate = null;
            e.printStackTrace();
        }

        ArrayList conversionUnits = new ArrayList();
        ArrayList times = new ArrayList();
        ArrayList factor = new ArrayList();

        String headerDivider = "<td class=\"yfnc_tablehead1\" align=\"center\"><b>";
        int start = loadedUpdate.indexOf("Last&nbsp;Trade");
        int lastStart = start;
        start = loadedUpdate.indexOf(headerDivider, start) + headerDivider.length();
        while (start > 0) {
            String data = getNextPlainData(loadedUpdate, start);
            conversionUnits.add(htmlToUnicode(data));

            start = loadedUpdate.indexOf(data, start) + data.length();
            String time = getNextPlainData(loadedUpdate, start);
            times.add(time);

            lastStart = start;
            start = loadedUpdate.indexOf(headerDivider, start);
            if (start > 0) {
                start += headerDivider.length();
            }
        }
        String columnIdentifier = "class=\"yfnc_tabledata1\"";
        start = loadedUpdate.indexOf(columnIdentifier, lastStart) + columnIdentifier.length();
        int end = loadedUpdate.indexOf("</tr>", start);
        while (start < end) {
            start = loadedUpdate.indexOf(">", start) + 1;
            String data = getNextPlainData(loadedUpdate, start);
            factor.add(data);
            start = loadedUpdate.indexOf(columnIdentifier, start) + columnIdentifier.length();
        }

        try {
            for (int i = 0; i < conversionUnits.size(); i++) {
                ct.addConversion(Conversion.createInstance(conversionUnits.get(0).toString(), "",
                    conversionUnits.get(i).toString(), "", factor.get(i).toString(), 0));
            }

            Iterator iter = times.iterator();
            Date minDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            while (iter.hasNext()) {
                String next = iter.next().toString();
                try {
                    Date date = new SimpleDateFormat("MM/dd/yyyy hh:mma z").parse(dateFormat.format(minDate) + " "
                            + next.trim().replaceAll("ET", "EST"));
                    if (date.before(minDate)) {
                        minDate = date;
                    }
                } catch (Exception e) {
                    System.out.println("Failed to parse " + next);
                }
            }
            setLastUpdated(minDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
