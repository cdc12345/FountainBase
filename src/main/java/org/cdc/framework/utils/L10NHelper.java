package org.cdc.framework.utils;

public class L10NHelper {
	public static String getProcedureKey(String procedureName){
		return "blockly.block." + procedureName;
	}

	public static String getProcedureToolTipKey(String procedureName){
		return "blockly.block." + procedureName + ".tooltip";
	}

	public static String getProcedureCategoryKey(String category){
		return "blockly.category." + category;
	}

	public static String getTriggerKey(String triggerName){
		return "trigger." + triggerName;
	}

	public static String getWarningKey(String warningKey){
		return "blockly.warning." + warningKey;
	}

	public static String getCustomVariableDependencyKey(String variableType){
		return "blockly.block.custom_dependency_" + variableType;
	}

	public static String getDataListKey(String datalistName){
		return "dialog.selector." + datalistName + ".message";
	}
}
