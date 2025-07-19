package org.cdc.framework.utils;

public enum BuiltInBlocklyColor {
    LOGIC,MATH,TEXTS;

    public static final String ENTITY_COLOR = "195";
    public static final String ITEMSTACK_COLOR = "250";

    /**
     *
     * @param hue 0,360
     * @return color
     */
    public String toString(int hue){
        return ColorUtils.colorHue(name().toUpperCase(),Math.clamp(hue,0,360));
    }

    public String toString(){
        return ColorUtils.colorHue(name().toUpperCase());
    }
}
