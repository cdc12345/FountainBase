package org.cdc.framework.utils;

import org.cdc.framework.interfaces.IProcedureCategory;

public class BuiltInToolBoxId {
	public static class AITasks {
		public static final String COMBAT_TASKS = "combattasks";
		public static final String BASIC_TASKS = "basictasks";
		public static final String MOVEMENT_TASKS = "movementtasks";
		public static final String OTHER_TASKS = "othertasks";
		public static final String PARENT_OWNER_TASKS = "parentownertasks";
		public static final String TARGET_TASKS = "targettasks";
		public static final String VILLAGE_TASKS = "villagetasks";
	}

	public enum Procedure implements IProcedureCategory {
		ENTITY_DATA("entitydata", BuiltInBlocklyColor.ENTITY_COLOR), ENTITY_MANAGEMENT("entitymanagement",
				BuiltInBlocklyColor.ENTITY_COLOR), ENTITY_PROCEDURES("entityprocedures",
				BuiltInBlocklyColor.ENTITY_COLOR), MATH("math", BuiltInBlocklyColor.MATH.toString()), TEXT("text",
				BuiltInBlocklyColor.TEXTS.toString()), ITEM_MANAGEMENT("itemmanagement",
				BuiltInBlocklyColor.ITEMSTACK_COLOR), ITEM_DATA("itemdata",
				BuiltInBlocklyColor.ITEMSTACK_COLOR), ITEM_PROCEDURES("itemprocedures",
				BuiltInBlocklyColor.ITEMSTACK_COLOR), BLOCK_DATA("blockdata",
				BuiltInBlocklyColor.BLOCK_COLOR), BLOCK_PROCEDURES("blockprocedures",
				BuiltInBlocklyColor.BLOCK_COLOR), BLOCK_ACTIONS("blockactions",
				BuiltInBlocklyColor.BLOCK_COLOR), WORLD_DATA("worlddata",
				BuiltInBlocklyColor.WORLD_COLOR), WORLD_MANAGEMENT("worldmanagement",
				BuiltInBlocklyColor.WORLD_COLOR), WORLD_PROCEDURES("worldprocedures",
				BuiltInBlocklyColor.WORLD_COLOR), PLAYER_DATA("playerdata",
				BuiltInBlocklyColor.PLAYER_COLOR), PLAYER_MANAGEMENT("playermanagement",
				BuiltInBlocklyColor.PLAYER_COLOR), PLAYER_PROCEDURES("playerprocedures",
				BuiltInBlocklyColor.PLAYER_COLOR), DIRECTION_ACTIONS("directionactions",
				BuiltInBlocklyColor.DIRECTION_ACTION_COLOR), GUI_MANAGEMENT("guimanagement",
				BuiltInBlocklyColor.GUI_MANAGEMENT_COLOR), DAMAGE_SOURCES("damagesources",
				BuiltInBlocklyColor.DAMAGE_SOURCE_COLOR), ENERGY_AND_FLUID("energyandfluid",
				BuiltInBlocklyColor.ENERGY_FLUID_COLOR), PROJECTILE_MANAGEMENT("projectilemanagement",
				BuiltInBlocklyColor.PROJECTILE_COLOR);
		public static final String OTHER = "other";
		public static final String APIS = "apis";
		public static final String MC_ELEMENTS = "mcelements";
		public static final String MC_VARIABLES = "mcvariables";
		public static final String LOGIC_LOOP = "logicloops";
		public static final String LOGIC_OPERATION = "logicoperations";
		public static final String ADVANCED = "advanced";
		public static final String CUSTOM_VARIABLES = "customvariables";

		private final String name;
		private final String defaultColor;

		Procedure(String name, String defaultColor) {
			this.name = name;
			this.defaultColor = defaultColor;
		}

		@Override public String getName() {
			return name;
		}

		@Override public String getDefaultColor() {
			return defaultColor;
		}
	}
}
