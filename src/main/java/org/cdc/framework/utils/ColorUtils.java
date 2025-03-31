package org.cdc.framework.utils;

import java.awt.*;

public class ColorUtils {
    public static String toHex(Color color){
        return "#" + (Integer.toHexString(color.getRGB())).substring(2);
    }

    public static String colorHue(String name){
        return "{BKY_"+name+"_HUE}";
    }

    public static String colorHue(String name,int hue){
        return "{BKY_"+name+"_"+Math.clamp(hue,0,360)+"}";
    }
}
