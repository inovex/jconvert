package com.edsdev.jconvert.common;

import java.util.Date;

import com.edsdev.jconvert.domain.ConversionType;

/**
 * This interface needs to be implemented if you are to tie in your own custom conversion code into JConvert. JConvert
 * will ask as series of questions of the class implementing this interface. It will ask you for your conversion data
 * and the time it was last updated. It will also attempt to register itself with you as a listener. This is handy if
 * you have a thread running in the background that updates data periodically. When you get this data you can fire an
 * event off to the listener, prompting it to get the new data.
 * 
 * @author Ed Sarrazin Created on Oct 26, 2007 6:35:19 AM
 */
public interface CustomConversionDataInterface {
    /**
     * This method will do what it needs to do to go out, read, etcetera the information it has to return ConversionType
     * that if fully populated with the custom conversions
     * 
     * @return ConversionType fully populated with the appropriate data
     */
    public ConversionType getConversions();

    /**
     * The internal mechanisms in the implementing class are unknown, but depending on how it works, this method should
     * always return the date that this data is accurate to. For instance, if it is hard coded data, you may want to
     * return the current date every time. If you are caching data from a website, you will want to store the last date
     * that the data was pulled and return it here.
     * 
     * @return Date of the last update.
     */
    public Date getLastUpdated();

    /**
     * Adds a listener for so that you can inform others to call getConversions() because there is an update
     * 
     * @param listener
     */
    public void addDataUpdatedListener(CustomDataUpdatedListener listener);
}
