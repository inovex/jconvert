package com.edsdev.jconvert.domain;

public class FractionalConversion extends Conversion {

    private long fromToTopFactor = 1;

    private long fromToBottomFactor = 1;

    public FractionalConversion(String fromUnit, String fromUnitAbbr, String toUnit, String toUnitAbbr,
            String fromToFactor, double fromToOffset) {
        super(fromUnit, fromUnitAbbr, toUnit, toUnitAbbr, fromToFactor, fromToOffset);
        setFromToFactorString(fromToFactor);
    }

    /**
     * @param value
     *            double value you want to convert
     * @param fromUnit
     *            Unit that you want to convert from
     * @return answer
     * 
     * If the fromUnit does not match the classes from unit, then it is assumed that you are converting the other way
     * ex. System.out.println(conversion.convertValue(17, conversion.getFromUnit()));
     * 
     */
    public double convertValue(double value, String pFromUnit) {
        if (pFromUnit.equals(this.getFromUnit())) {
            return getRoundedResult(((value * fromToTopFactor) / fromToBottomFactor) + getFromToOffset());
        } else {
            return getRoundedResult(((value - getFromToOffset()) * fromToBottomFactor) / fromToTopFactor);
        }
    }

    public String multiply(Conversion byConversion) {
        String rv = "1";
        if (byConversion instanceof FractionalConversion) {
            FractionalConversion fc = (FractionalConversion) byConversion;
            rv = (this.getFromToTopFactor() * fc.getFromToTopFactor()) + "/"
                    + (this.getFromToBottomFactor() * fc.getFromToBottomFactor());
        } else {
            rv = ((this.getFromToTopFactor() * byConversion.getFromToFactor()) / this.getFromToBottomFactor()) + "";
        }
        return rv;
    }

    public String divide(Conversion byConversion) {
        String rv = "1";
        if (byConversion instanceof FractionalConversion) {
            FractionalConversion fc = (FractionalConversion) byConversion;
            rv = (this.getFromToTopFactor() * fc.getFromToBottomFactor()) + "/"
                    + (this.getFromToBottomFactor() * fc.getFromToTopFactor());
        } else {
            rv = (this.getFromToBottomFactor() * byConversion.getFromToFactor()) / this.getFromToTopFactor() + "";
        }
        return rv;
    }

    public long getFromToBottomFactor() {
        return fromToBottomFactor;
    }

    public void setFromToBottomFactor(long fromToBottomFactor) {
        this.fromToBottomFactor = fromToBottomFactor;
    }

    public long getFromToTopFactor() {
        return fromToTopFactor;
    }

    public void setFromToTopFactor(long fromToTopFactor) {
        this.fromToTopFactor = fromToTopFactor;
    }

    /* (non-Javadoc)
     * @see com.edsdev.jconvert.domain.Conversion#getFromToFactor()
     */
    public double getFromToFactor() {
        //TODO do appropriate exception handling here
        throw new RuntimeException("Not Supported");
    }

    /* (non-Javadoc)
     * @see com.edsdev.jconvert.domain.Conversion#setFromToFactor(double)
     */
    public void setFromToFactor(double fromToFactor) {
        //TODO do appropriate exception handling here
        throw new RuntimeException("Not Supported");
    }

    /* (non-Javadoc)
     * @see com.edsdev.jconvert.domain.Conversion#setFromToFactorString(java.lang.String)
     */
    public void setFromToFactorString(String fromToFactor) {
        int pos = fromToFactor.indexOf("/");
        if (pos > 0) {
            String top = fromToFactor.substring(0, pos);
            String bottom = fromToFactor.substring(pos + 1);
            fromToTopFactor = getLong(top);
            fromToBottomFactor = getLong(bottom);
        } else if (isWholeNumber(fromToFactor)) {
            //representing a whole number like this helps preserve fractions - bit of a trick
            fromToTopFactor = getLong(fromToFactor);
            fromToBottomFactor = 1;
        } else {
            System.err.println("Tried to process decimal as fraction:" + fromToFactor);
            //TODO throw proper exception here
        }
    }

    /* (non-Javadoc)
     * @see com.edsdev.jconvert.domain.Conversion#getFromToFactorString()
     */
    public String getFromToFactorString() {
        return fromToTopFactor + "/" + fromToBottomFactor;
    }

}
