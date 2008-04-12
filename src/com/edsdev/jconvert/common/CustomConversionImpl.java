package com.edsdev.jconvert.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * This is the custom conversion interface that must be implemented to add a custom conversion panel to JConvert.
 * 
 * @author Ed Sarrazin Created on Oct 29, 2007 5:05:58 PM
 */
public abstract class CustomConversionImpl implements CustomConversionDataInterface {
    private Date lastUpdated = null;

    private ArrayList listeners = new ArrayList();

    /*
     * (non-Javadoc)
     * 
     * @see com.edsdev.jconvert.common.CustomConversionDataInterface#getLastUpdated()
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date date) {
        lastUpdated = date;
    }

    public void addDataUpdatedListener(CustomDataUpdatedListener listener) {
        listeners.add(listener);
    }

    public void fireDataUpdatedEvent() {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ((CustomDataUpdatedListener) iter.next()).customDataUpdated();
        }
    }

    public String getNextPlainData(StringBuffer buff, int start) {
        while (buff.substring(start, start + 1).equals("<")) {
            start = buff.indexOf(">", start + 1) + 1;
        }
        String rv = buff.substring(start, buff.indexOf("<", start));
        return rv;
    }

    public String htmlToUnicode(String startValue) {
        StringBuffer rv = new StringBuffer();
        int pos = startValue.indexOf("&#");
        int lastPos = 0;
        if (pos < 0) {
            rv.append(startValue);
        }
        while (pos >= 0) {
            rv.append(startValue.substring(lastPos, pos));
            lastPos = startValue.indexOf(";", pos) + 1;
            String temp = startValue.substring(pos + 2, lastPos - 1);
            String hex = Integer.toHexString(new Integer(temp).intValue());
            rv.append("\\u00" + hex.toUpperCase());
            pos = startValue.indexOf("&#", lastPos);
            if (pos < 0) {
                rv.append(startValue.substring(lastPos));
            }
        }
        return rv.toString();
    }

}
