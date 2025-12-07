package org.cdc.framework.utils.yaml;

/**
 * A util to optimise the hardcode
 */
public class YamlDataUtils {

	public static String NULL = "null";
	public static String lineSeparator = "\n";

	public static String valuePrefix = "- ";
	public static String keySuffix = ": ";

	public static String str(String value) {
		return "\"" + value + "\"";
	}

	public static String keyAndValue(String key, String value) {
		return key + keySuffix + value;
	}
}
