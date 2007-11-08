package com.edsdev.jconvert.presentation;

import com.edsdev.jconvert.util.Messages;

/**
 * This class is used for creating presentation views of the data.
 * 
 * @author Ed Sarrazin Created on Aug 14, 2007 4:46:03 PM
 */
public class ConversionUnitData implements Comparable {
    public String getTranslatedUnit() {
        return translatedUnit;
    }

    public void setTranslatedUnit(String translatedUnit) {
        this.translatedUnit = translatedUnit;
    }

    public String getTranslatedUnitAbbrev() {
        return translatedUnitAbbrev;
    }

    public void setTranslatedUnitAbbrev(String translatedUnitAbbrev) {
        this.translatedUnitAbbrev = translatedUnitAbbrev;
    }

    private String unit;

    private String translatedUnit;

    private String unitAbbrev;

    private String translatedUnitAbbrev;

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

        this.translatedUnit = Messages.getUnitTranslation(unit);
        this.translatedUnitAbbrev = Messages.getUnitTranslation(unitAbbrev);
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
