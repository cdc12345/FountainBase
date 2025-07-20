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

	public static enum Procedure implements IProcedureCategory {
		ENTITY_DATA("entitydata",BuiltInBlocklyColor.ENTITY_COLOR),ENTITY_MANAGEMENT("entitymanagement",BuiltInBlocklyColor.ENTITY_COLOR);
		public static final String OTHER = "other";
		public static final String APIS = "apis";
		public static final String MC_ELEMENTS = "mcelements";
		public static final String MC_VARIABLES = "mcvariables";
		public static final String LOGIC_LOOP = "logicloops";
		public static final String LOGIC_OPERATION = "logicoperations";
		public static final String MATH = "math";
		public static final String TEXT = "text";
		public static final String ADVANCED = "advanced";
		public static final String CUSTOM_VARIABLES = "customvariables";
		public static final String ENTITY_PROCEDURES = "entityprocedures";
		public static final String BLOCK_ACTIONS = "blockactions";
		public static final String BLOCK_DATA = "blockdata";
		public static final String BLOCK_PROCEDURES = "blockprocedures";
		public static final String WORLD_DATA = "worlddata";
		public static final String WORLD_MANAGEMENT = "worldmanagement";
		public static final String WORLD_PROCEDURES = "worldprocedures";
		public static final String PLAYER_DATA = "playerdata";
		public static final String PLAYER_MANAGEMENT = "playermanagement";
		public static final String PLAYER_PROCEDURES = "playerprocedures";
		public static final String ITEM_DATA = "itemdata";
		public static final String ITEM_MANAGEMENT = "itemmanagement";
		public static final String ITEM_PROCEDURES = "itemprocedures";

		private final String name;
		private final String defaultColor;

		Procedure(String name,String defaultColor){
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
