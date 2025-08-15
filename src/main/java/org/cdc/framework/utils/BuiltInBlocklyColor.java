package org.cdc.framework.utils;

public enum BuiltInBlocklyColor {
	LOGIC, MATH, TEXTS;

	public static final String ENTITY_COLOR = "195";
	public static final String ITEMSTACK_COLOR = "250";
	public static final String BLOCK_COLOR = "60";
	public static final String ENERGY_FLUID_COLOR = "60";
	public static final String DAMAGE_SOURCE_COLOR = "320";
	public static final String DIRECTION_ACTION_COLOR = "20";
	public static final String GUI_MANAGEMENT_COLOR = "110";
	public static final String PLAYER_COLOR = "175";
	public static final String PROJECTILE_COLOR = "300";
	public static final String WORLD_COLOR = "35";

	/**
	 *
	 * @param hue 0,360
	 * @return color
	 */
	public String toString(int hue) {
		return ColorUtils.colorHue(name().toUpperCase(), Math.clamp(hue, 0, 360));
	}

	public String toString() {
		return ColorUtils.colorHue(name().toUpperCase());
	}
}
