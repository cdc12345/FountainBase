package org.cdc.framework.utils;

import org.cdc.framework.interfaces.IVariableType;

public enum BuiltInTypes implements IVariableType {
	Number("number", "Number"), Direction("direction", "Direction"), Entity("entity", "Entity"), ItemStack("itemstack",
			"MCItem"), Boolean("logic", "Boolean"), String("string", "String"), BlockState("blockstate",
			"MCItemBlock"), DamageSource("damagesource", "DamageSource"), ActionResultType("actionresulttype",
			"ActionResultType"), World("world", "");

	public static BuiltInTypes getType(String name) {
		for (BuiltInTypes types : BuiltInTypes.values()) {
			if (types.higherName.equals(name) || types.lowerName.equals(name)) {
				return types;
			}
		}
		return null;
	}

	private final String lowerName;
	private final String higherName;

	BuiltInTypes(String lowerName, String higherName) {
		this.lowerName = lowerName;
		this.higherName = higherName;
	}

	public String getHigherName() {
		return higherName;
	}

	public String getLowerName() {
		return lowerName;
	}

	@Override public java.lang.String getBlocklyVariableType() {
		return getHigherName();
	}

	@Override public java.lang.String getVariableType() {
		return getLowerName();
	}
}
