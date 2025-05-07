package org.cdc.framework.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.interfaces.IProcedureCategory;
import org.cdc.framework.interfaces.IVariableType;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.ColorUtils;
import org.cdc.framework.utils.FileUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProcedureBuilder extends JsonBuilder implements IGeneratorInit {
	private boolean isType;

	private String colorKey;

	private final JsonObject mcreator;
	private final JsonArray inputs;
	private final JsonArray fields;
	private final JsonArray statements;
	private final JsonArray args0;
	private final JsonArray dependencies;
	private final JsonArray requiredApis;
	private final JsonArray extensions;
	private final JsonArray warnings;

	public ProcedureBuilder(File rootPath) {
		this(rootPath, "procedures");
	}

	public ProcedureBuilder(File rootPath, String child) {
		super(rootPath, new File(rootPath, child));

		this.colorKey = "colour";

		this.result = new JsonObject();

		this.mcreator = new JsonObject();
		this.inputs = new JsonArray();
		this.fields = new JsonArray();
		this.statements = new JsonArray();
		this.dependencies = new JsonArray();
		this.extensions = new JsonArray();
		this.requiredApis = new JsonArray();
		this.warnings = new JsonArray();

		this.args0 = new JsonArray();
		this.result.getAsJsonObject().add("args0", args0);
		this.result.getAsJsonObject().add("extensions", extensions);

	}

	public ProcedureBuilder appendJsonElement(String name, JsonElement jsonElement) {
		this.result.getAsJsonObject().add(name, jsonElement);
		return this;
	}

	public ProcedureBuilder setName(String name) {
		if (isType && !name.startsWith("$")) {
			this.fileName = "$" + FileUtils.filterSpace(name);
		} else {
			this.fileName = FileUtils.filterSpace(name);
		}
		return this;
	}

	public ProcedureBuilder markType() {
		colorKey = "color";
		isType = true;
		result.getAsJsonObject().remove("mcreator");
		result.getAsJsonObject().remove("args0");
		result.getAsJsonObject().remove("extensions");
		if (fileName != null) {
			setName(fileName);
		}
		return this;
	}

	public ProcedureBuilder setColor(int color) {
		result.getAsJsonObject().add(colorKey, new JsonPrimitive(color));
		return this;
	}

	public ProcedureBuilder setColor(String color) {
		result.getAsJsonObject().add(colorKey, new JsonPrimitive(color));
		return this;
	}

	public ProcedureBuilder setColor(Color color) {
		return setColor(ColorUtils.toHex(color));
	}

	public ProcedureBuilder setParentCategory(String parentCategory) {
		if (isType)
			result.getAsJsonObject().add("parent_category", new JsonPrimitive(parentCategory));
		return this;
	}

	public ProcedureBuilder setInputsInline(boolean inputsInline) {
		result.getAsJsonObject().add("inputsInline", new JsonPrimitive(inputsInline));
		return this;
	}

	public ProcedureBuilder setPreviousStatement(String previousStatement) {
		result.getAsJsonObject().addProperty("previousStatement", previousStatement);
		return this;
	}

	public ProcedureBuilder setNextStatement(String nextStatement) {
		result.getAsJsonObject().addProperty("nextStatement", nextStatement);
		return this;
	}

	/**
	 * "output": "Boolean"
	 *
	 * @param higherName 输出类型,必须第一个字母大写
	 * @return this
	 */
	public ProcedureBuilder setOutput(String higherName) {
		result.getAsJsonObject().addProperty("output", higherName);
		return this;
	}

	public ProcedureBuilder setOutput(IVariableType variableType) {
		return setOutput(variableType.getBlocklyVariableType());
	}

	/**
	 * 会把第一个参数作为返回值 "output": ["Boolean","String"]
	 *
	 * @param higherNames 输出类型,必须第一个字母大写
	 * @return this
	 */
	public ProcedureBuilder setOutput(String... higherNames) {
		JsonArray jsonElements = new JsonArray();
		for (String name : higherNames) {
			jsonElements.add(name);
		}
		result.getAsJsonObject().add("output", jsonElements);
		return this;
	}

	public ProcedureBuilder setOutput(IVariableType... higherNames) {
		JsonArray jsonElements = new JsonArray();
		for (IVariableType name : higherNames) {
			jsonElements.add(name.getBlocklyVariableType());
		}
		result.getAsJsonObject().add("output", jsonElements);
		return this;
	}

	public ProcedureBuilder setGroup(String group) {
		mcreator.addProperty("group", group);
		return this;
	}

	public ProcedureBuilder setToolBoxId(String toolBoxId) {
		mcreator.addProperty("toolbox_id", toolBoxId);
		return this;
	}

	public ProcedureBuilder setToolBoxId(IProcedureCategory category) {
		return setToolBoxId(category.getName());
	}

	public ProcedureBuilder appendToolBoxInit(String init) {
		if (!mcreator.has("toolbox_init"))
			mcreator.add("toolbox_init", new JsonArray());
		mcreator.get("toolbox_init").getAsJsonArray().add(init);
		return this;
	}

	public ProcedureBuilder setCategory(IProcedureCategory category) {
		return setToolBoxId(category);
	}

	public ProcedureBuilder setCategory(String category) {
		return setToolBoxId(category);
	}

	public ToolBoxInitBuilder toolBoxInitBuilder() {
		return new ToolBoxInitBuilder();
	}

	public ProcedureBuilder appendArgs0Element(JsonElement jsonElement) {
		args0.add(jsonElement);
		return this;
	}

	public ProcedureBuilder appendArgs0InputValue(String name, String higherName) {
		return appendArgs0InputValue(name, higherName, true);
	}

	public ProcedureBuilder appendArgs0InputValue(String name, IVariableType type) {
		return appendArgs0InputValue(name, type, true);
	}

	public ProcedureBuilder appendArgs0InputValue(String name, IVariableType type, boolean addToInputs) {
		return appendArgs0InputValue(name, type.getBlocklyVariableType(), addToInputs);
	}

	/**
	 * { "type": "input_value", "name": "entity", "check": "Entity" }
	 *
	 * @param name        名字
	 * @param higherName  这个是检查类型,必须是Object这样的
	 * @param addToInputs 是否自动添加到inputs
	 * @return this
	 */
	public ProcedureBuilder appendArgs0InputValue(String name, String higherName, boolean addToInputs) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "input_value");
		jsonObject.addProperty("name", name);
		if (higherName != null) {
			jsonObject.addProperty("check", higherName);
		}
		if (addToInputs)
			inputs.add(name);
		return appendArgs0Element(jsonObject);
	}

	public ProcedureBuilder appendArgs0FieldInput(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_input");
		jsonObject.addProperty("name", name);
		fields.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * 添加语句
	 *
	 * @param name 语句名称
	 * @return this
	 */
	public ProcedureBuilder appendArgs0StatementInput(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "input_statement");
		jsonObject.addProperty("name", name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * { "type": "field_checkbox", "name": "insight", "checked": false },
	 *
	 * @return this
	 */
	public ProcedureBuilder appendArgs0FieldCheckbox(String name, boolean checked) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_checkbox");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("checked", checked);
		appendArgs0Element(jsonObject);
		fields.add(name);
		return this;
	}

	/**
	 * { "type": "field_data_list_selector", "name": "entity", "datalist": "entity",
	 * "testValue": "EntityCreeper" },
	 *
	 * @return this
	 */
	public ProcedureBuilder appendArgs0FieldDataListSelector(String name, String dataList, String testValue) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_data_list_selector");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("datalist", dataList);
		jsonObject.addProperty("testValue", testValue);
		appendArgs0Element(jsonObject);
		fields.add(name);
		return this;
	}

	/**
	 * { "type": "field_ai_condition_selector", "name": "condition" }
	 *
	 * @param name 名字
	 * @return this
	 */
	public ProcedureBuilder appendArgs0FieldAIConditionSelector(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_ai_condition_selector");
		jsonObject.addProperty("name", name);
		fields.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * 占位置用的(
	 *
	 * @param name name
	 * @return this
	 */
	public ProcedureBuilder appendArgs0InputDummy(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "input_dummy");
		jsonObject.addProperty("name", name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * 比如服务器图标啥的(
	 *
	 * @param src    resource url
	 * @param width  width
	 * @param height height
	 * @return this
	 */
	public ProcedureBuilder appendArgs0FieldImage(String src, int width, int height) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_image");
		jsonObject.addProperty("src", src);
		jsonObject.addProperty("width", width);
		jsonObject.addProperty("height", height);
		return appendArgs0Element(jsonObject);
	}

	public ProcedureBuilder appendArgs0FieldDropDown(String name,String... map){
		LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
		for (int index = 0; index < (map.length /2) ;index++){
			linkedHashMap.put(map[index * 2],map[index * 2 + 1]);
		}
		return appendArgs0FieldDropDown(name,linkedHashMap);
	}

	public ProcedureBuilder appendArgs0FieldDropDown(String name, Map<String, String> options) {
		ArrayList<JsonElement> jsonElements = new ArrayList<>();
		for (Map.Entry<String, String> entry : options.entrySet()) {
			JsonArray jsonArray = new JsonArray();
			jsonArray.add(entry.getKey());
			jsonArray.add(entry.getValue());
			jsonElements.add(jsonArray);
		}
		return appendArgs0FieldDropDown(name, jsonElements.toArray(new JsonElement[0]));
	}

	/**
	 * {
	 * "type": "field_dropdown",
	 * "name": "type",
	 * "options": [
	 * [
	 * "one player",
	 * "player"
	 * ],
	 * [
	 * "players",
	 * "players"
	 * ],
	 * [
	 * "one entity",
	 * "entity"
	 * ],
	 * [
	 * "entities",
	 * "entities"
	 * ]
	 * ]
	 * },
	 *
	 * @param name
	 * @return
	 */
	public ProcedureBuilder appendArgs0FieldDropDown(String name, JsonElement... options) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_dropdown");
		jsonObject.addProperty("name", name);
		JsonArray options1 = new JsonArray();
		for (JsonElement jsonElement : options) {
			if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
				JsonArray jsonElements = new JsonArray();
				jsonElements.add(jsonPrimitive);
				jsonElements.add(jsonPrimitive);
				options1.add(jsonElements);
			} else {
				options1.add(jsonElement);
			}
		}
		jsonObject.add("options", options1);
		fields.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * {
	 * "type": "field_mcitem_selector",
	 * "name": "block",
	 * "supported_mcitems": "allblocks"
	 * },
	 *
	 * @param name
	 * @return
	 */
	public ProcedureBuilder appendArgs0FieldMCItemSelector(String name, String supported) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_mcitem_selector");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("field_mcitem_selector", supported);
		fields.add(name);
		return this;
	}

	/**
	 * @param jsonObject element
	 * @return this
	 * @apiNote append
	 */
	@CanIgnoreReturnValue public ProcedureBuilder appendStatement(JsonElement jsonObject) {
		statements.add(jsonObject);
		return this;
	}

	public StatementBuilder statementBuilder() {
		return new StatementBuilder();
	}

	public ProcedureBuilder appendExtension(String extension) {
		extensions.add(extension);
		return this;
	}

	/**
	 * "dependencies": [ { "name": "world", "type": "world" } ]
	 *
	 * @param jsonObject json
	 * @return this
	 */
	public ProcedureBuilder appendDependency(JsonObject jsonObject) {
		dependencies.add(jsonObject);
		return this;
	}

	/**
	 * "dependencies": [ { "name": "world", "type": "world" } ]
	 *
	 * @param name      名称
	 * @param lowerName 类型,比如全部小写
	 * @return this
	 */
	public ProcedureBuilder appendDependency(String name, String lowerName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("type", lowerName.toLowerCase());
		return appendDependency(jsonObject);
	}

	public ProcedureBuilder appendDependency(String name, IVariableType type) {
		return appendDependency(name, type.getVariableType());
	}

	public ProcedureBuilder appendRequiredApi(String name) {
		requiredApis.add(name);
		return this;
	}

	/**
	 * "warnings": [ "place_feature_ghost_blocks" ]
	 *
	 * @param key warningKey
	 * @return this
	 */
	public ProcedureBuilder appendWarning(String key) {
		warnings.add(key);
		return this;
	}

	public ProcedureBuilder appendWarning(String key, LanguageBuilder languageBuilder, String value) {
		warnings.add(key);
		languageBuilder.appendWarning(key, value);
		return this;
	}

	@CanIgnoreReturnValue public ProcedureBuilder setLanguage(LanguageBuilder languageBuilder, String value) {
		if (isType)
			languageBuilder.appendProcedureCategory(fileName.substring(1), value);
		else {
			Pattern var = Pattern.compile("%\\d");
			var ma = var.matcher(value);
			int count = 0;
			while (ma.find()) {
				count++;
			}
			if (count != args0.size()) {
				throw new RuntimeException(
						"\" " + value + " \"is not a regular content because we need parameter count: " + args0.size());
			}
			languageBuilder.appendProcedure(fileName, value);
		}
		return this;
	}

	public ProcedureBuilder setToolTip(LanguageBuilder languageBuilder, String value) {
		if (!isType) {
			languageBuilder.appendProcedureToolTip(fileName, value);
		}
		return this;
	}

	public ProcedureBuilder initGenerator() {
		MCreatorPluginFactory.generatorInits.add(this);
		return this;
	}

	public void initGenerator0(String generatorName,boolean replace) {
		if (isType) {
			return;
		}
		File generator = new File(rootPath, generatorName);
		File procedures = new File(generator, targetPath.getName());
		if (!procedures.exists()) {
			procedures.mkdirs();
		}
		try {
			String builder = BuilderUtils.generateInputsComment(inputs) + System.lineSeparator()
					+ BuilderUtils.generateStatementsComment(statements) + System.lineSeparator()
					+ BuilderUtils.generateFieldsComment(fields);
			Files.copy(new ByteArrayInputStream(builder.getBytes(StandardCharsets.UTF_8)),
					new File(procedures, fileName + ".java.ftl").toPath());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	@Override public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
		return rootPath.equals(mCreatorPluginFactory.rootPath()) && BuilderUtils.isSupportProcedure(
				mCreatorPluginFactory.getCurrentInit());
	}

	@Override public JsonElement build() {
		if (isType){
			return this.result;
		}

		if (!result.getAsJsonObject().has("inputsInline")){
			setInputsInline(true);
		}
		if (!mcreator.has("group")){
			setGroup("name");
		}
		if (!mcreator.has("toolbox_id")){
			setToolBoxId(BuiltInToolBoxId.Procedure.OTHER);
		}
		// root
		if (extensions.isEmpty()) {
			this.result.getAsJsonObject().remove("extensions");
		}
		if (args0.isEmpty()) {
			this.result.getAsJsonObject().remove("args0");
		}
		// mcreator
		if (!inputs.isEmpty()) {
			this.mcreator.add("inputs", inputs);
		}
		if (!fields.isEmpty()) {
			this.mcreator.add("fields", fields);
		}
		if (!statements.isEmpty()) {
			this.mcreator.add("statements", statements);
		}
		if (!dependencies.isEmpty()) {
			mcreator.add("dependencies", dependencies);
		}
		if (!requiredApis.isEmpty()) {
			mcreator.add("required_apis", requiredApis);
		}
		if (!warnings.isEmpty()) {
			mcreator.add("warnings", warnings);
		}

		if (!mcreator.isEmpty()) {
			this.result.getAsJsonObject().add("mcreator", mcreator);
		}
		return result;
	}

	public class StatementBuilder extends JsonBuilder {

		private final JsonArray provides;

		protected StatementBuilder() {
			super(null, null);
			this.result = new JsonObject();

			this.provides = new JsonArray();
		}

		public StatementBuilder setName(String name) {
			result.getAsJsonObject().addProperty("name", name);
			return this;
		}

		/**
		 * @param name      名称
		 * @param lowerName 类型,必须全部小写
		 * @return this
		 */
		public StatementBuilder appendProvide(String name, String lowerName) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", name);
			jsonObject.addProperty("type", lowerName.toLowerCase());
			provides.add(jsonObject);
			return this;
		}

		public StatementBuilder appendProvide(String name, IVariableType type) {
			return appendProvide(name, type.getVariableType());
		}

		@Override public JsonElement build() {
			if (!provides.isEmpty()) {
				this.result.getAsJsonObject().add("provides", provides);
			}
			return result;
		}

		public ProcedureBuilder buildAndReturn() {
			ProcedureBuilder.this.appendStatement(build());
			return ProcedureBuilder.this;
		}
	}

	public class ToolBoxInitBuilder {
		private final String placeholder = "﨎";
		private String result = placeholder;

		protected ToolBoxInitBuilder appendElement(String element) {
			result = result.replace(placeholder, element);
			return this;
		}

		public ToolBoxInitBuilder setName(String name) {
			return appendElement("<value name=\"" + name + "\">" + placeholder + "</value>");
		}

		public ToolBoxInitBuilder appendDefaultEntity() {
			return appendElement("<block type=\"entity_from_deps\">" + placeholder + "</block>");
		}

		public ToolBoxInitBuilder appendDefaultItem() {
			return appendElement("<block type=\"itemstack_to_mcitem\">" + placeholder + "</block>");
		}

		public ToolBoxInitBuilder appendConstantNumber(int num) {
			return appendElement("<block type=\"math_number\"><field name=\"NUM\">" + num + "</field></block>");
		}

		public ToolBoxInitBuilder appendConstantString(String str) {
			return appendElement("<block type=\"text\"><field name=\"TEXT\">" + str + "</field></block>");
		}

		public ProcedureBuilder buildAndReturn() {
			result = result.replace(placeholder, "");
			return ProcedureBuilder.this.appendToolBoxInit(result);
		}
	}
}
