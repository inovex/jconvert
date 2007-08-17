package com.edsdev.jconvert.presentation;

/**
 * @author Ed S Created on Aug 14, 2007 4:46:03 PM
 */
public class ConversionUnitData implements Comparable {
    private String unit;

    private String unitAbbrev;

    private int generationAge = 0;

    /**
     * @param unit
     * @param unitAbbrev
     * @param generationAge
     */
    public ConversionUnitData(String unit, String unitAbbrev, int generationAge) {
        super();
        this.unit = unit;
        this.unitAbbrev = unitAbbrev;
        this.generationAge = generationAge;
    }

    public int getGenerationAge() {
        return generationAge;
    }

    public void setGenerationAge(int generationAge) {
        this.generationAge = generationAge;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitAbbrev() {
        return unitAbbrev;
    }

    public void setUnitAbbrev(String unitAbbrev) {
        this.unitAbbrev = unitAbbrev;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConversionUnitData)) {
            return false;
        }
        return this.getUnit().equals(((ConversionUnitData) obj).getUnit());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof ConversionUnitData)) {
            return -1;
        }
        return this.getUnit().compareTo(((ConversionUnitData) obj).getUnit());
    }

    public String toString() {
        if (this.getUnitAbbrev() == null) {
            return this.getUnit();
        } else {
            return this.getUnit() + " " + this.getUnitAbbrev();
        }
    }
    
    public int hashCode() {
        return this.getUnit().hashCode();
    }
}
