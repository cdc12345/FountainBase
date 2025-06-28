package org.cdc.framework.utils;

public class MCreatorVersions {
    public static class Test {
        public static final String V_2025_0 = "v_2025_0";
    }

    public static final String V_2025_1 = "v_2025_1";
    public static final String V_2025_1_9999 = "v_2025_199999";
    public static final String V_2025_2 = "v_2025_2";

    public static String toFormattedVersion(String origin){
        if (origin.startsWith("v_")){
			return origin.substring(2).replace("_","00");
        }
        return origin;
    }
}
