package org.cdc.framework.utils;

import java.awt.*;

public class ColorUtils {
    public static String toHex(Color color){
        return "#" + (Integer.toHexString(color.getRGB())).substring(2);
    }
}
