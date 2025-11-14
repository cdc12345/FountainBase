package org.cdc.framework.utils.yaml;

public class YamlDataUtils {

	public static String NULL = "null";
	public static String lineSeparator = "\n";

	public static String str(String value) {
		return "\"" + value + "\"";
	}

	public static boolean bool(int value) {
		return value == 1;
	}

	public static String keyAndValue(String key, String value) {
		return key + ": " + value;
	}
}
