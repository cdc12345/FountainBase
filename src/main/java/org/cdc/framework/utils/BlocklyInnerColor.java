package org.cdc.framework.utils;

import java.util.Locale;

public enum BlocklyInnerColor {
    LOGIC,MATH,TEXTS;

    /**
     *
     * @param hue 0,360
     * @return color
     */
    public String toString(int hue){
        return "%{BKY_"+this.name().toUpperCase(Locale.ROOT)+"_"+Math.clamp(hue,0,360)+"}";
    }
}
