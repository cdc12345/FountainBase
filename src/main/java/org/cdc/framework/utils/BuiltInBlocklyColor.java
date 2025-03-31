package org.cdc.framework.utils;

public enum BuiltInBlocklyColor {
    LOGIC,MATH,TEXTS;

    /**
     *
     * @param hue 0,360
     * @return color
     */
    public String toString(int hue){
        return ColorUtils.colorHue(name().toUpperCase(),hue);
    }

    public String toString(){
        return ColorUtils.colorHue(name().toUpperCase());
    }
}
