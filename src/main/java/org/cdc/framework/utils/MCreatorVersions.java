package org.cdc.framework.utils;

import org.jetbrains.annotations.TestOnly;

public class MCreatorVersions {
	@TestOnly
    public static class Test {
		/**
		 * Test version
		 */
        public static final String V_2025_0 = "v_2025_0";
    }

	public static final String V_2024_4 = "v_2024_4";
    public static final String V_2025_1 = "v_2025_1";
    public static final String V_2025_1_9999 = "v_2025_199999";
    public static final String V_2025_2 = "v_2025_2";
	public static final String V_2025_3 = "v_2025_3";
	public static final String V_2025_4 = "v_2025_4";

    public static String toFormattedVersion(String origin){
        if (origin.startsWith("v_")){
			return origin.substring(2).replace("_","00");
        }
        return origin;
    }

	public static String toDevelopingVersion(String origin){
		return toFormattedVersion(origin)+"99999";
	}
}
