package org.cdc.framework.utils;

import org.cdc.framework.builder.ProcedureBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorUtils {

    private static Map<String,String> colorMap = new HashMap<>();

    public static String getSuggestColor(String category){
        return colorMap.getOrDefault(category, ProcedureBuilder.color.toString());
    }

    public static void putSuggestColor(String category,String suggest){
        colorMap.put(category,suggest);
    }

    public static String toHex(Color color){
        return "#" + (Integer.toHexString(color.getRGB())).substring(2);
    }

    public static String colorHue(String name){
        return "%{BKY_"+name+"_HUE}";
    }

    public static String colorHue(String name,int hue){
        return "%{BKY_"+name+"_"+Math.clamp(hue,0,360)+"}";
    }
}
