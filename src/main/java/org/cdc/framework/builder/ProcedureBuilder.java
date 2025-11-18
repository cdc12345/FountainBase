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
import org.cdc.framework.interfaces.annotation.ProcedureCategoryLikeMethod;
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
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class ProcedureBuilder extends JsonBuilder implements IGeneratorInit {
	public static Object color = "35";
	public static String category = null;

	private final ArrayList<String> sequence = new ArrayList<>();

	protected final JsonObject mcreator;
	protected final JsonArray inputs;
	protected final JsonArray fields;
	protected final JsonArray statements;
	protected final JsonArray args0;
	protected final JsonArray dependencies;
	protected final JsonArray requiredApis;
	protected final JsonArray extensions;
	protected final JsonArray warnings;
	protected boolean isType;
	protected String colorKey;
	private final Flags flags;

	private static class Flags {
		private boolean flagToSetLang;
		private boolean flagToSetColor;
		private boolean flagToInitGenerator;
	}

	public ProcedureBuilder(File rootPath) {
		this(rootPath, "procedures");
	}

	public ProcedureBuilder(File rootPath, String childName) {
		super(rootPath, new File(rootPath, childName));

		this.flags = new Flags();
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

	@ProcedureCategoryLikeMethod public ProcedureBuilder setName(String name) {
		if (isType && !name.startsWith("$")) {
			this.fileName = "$" + FileUtils.filterSpace(name);
		} else {
			this.fileName = FileUtils.filterSpace(name);
		}
		return this;
	}

	/**
	 * should be first
	 *
	 * @return this
	 */
	@ProcedureCategoryLikeMethod public ProcedureBuilder markType() {
		String colorValue;
		if (result.getAsJsonObject().has(colorKey)) {
			colorValue = result.getAsJsonObject().get(colorKey).getAsString();
			result.getAsJsonObject().remove(colorKey);
			colorKey = "color";
			result.getAsJsonObject().add(colorKey, new JsonPrimitive(colorValue));
		} else {
			colorKey = "color";
		}
		isType = true;
		result.getAsJsonObject().remove("mcreator");
		result.getAsJsonObject().remove("args0");
		result.getAsJsonObject().remove("extensions");
		if (fileName != null) {
			setName(fileName);
		}
		return this;
	}

	@ProcedureCategoryLikeMethod public ProcedureBuilder setColor(int color) {
		flags.flagToSetColor = true;
		ProcedureBuilder.color = color;
		result.getAsJsonObject().add(colorKey, new JsonPrimitive(color));
		return this;
	}

	@ProcedureCategoryLikeMethod public ProcedureBuilder setColor(String color) {
		if (color == null) {
			return this;
		}
		ProcedureBuilder.color = color;
		flags.flagToSetColor = true;
		result.getAsJsonObject().add(colorKey, new JsonPrimitive(color));
		return this;
	}

	@ProcedureCategoryLikeMethod public ProcedureBuilder setColor(Color color) {
		return setColor(ColorUtils.toHex(color));
	}

	@ProcedureCategoryLikeMethod public ProcedureBuilder setParentCategory(String parentCategory) {
		if (isType)
			result.getAsJsonObject().add("parent_category", new JsonPrimitive(parentCategory));
		return this;
	}

	@ProcedureCategoryLikeMethod public ProcedureBuilder setParentCategory(IProcedureCategory procedureCategory) {
		return setParentCategory(procedureCategory.getName());
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
	 * @param higherName outputType the first letter should be uppercase
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
	 * "output": ["Boolean","String"]
	 * the first of the Array is the main outputType
	 *
	 * @param higherNames outputType the first letter should be uppercase
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

	public ProcedureBuilder setToolBoxId(IProcedureCategory category) {
		if (!flags.flagToSetColor) {
			setColor(category.getDefaultColor());
		}
		return setToolBoxId(category.getName());
	}

	public ProcedureBuilder setToolBoxId(String toolBoxId) {
		mcreator.addProperty("toolbox_id", toolBoxId);
		category = toolBoxId;
		return this;
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

	/**
	 * @param name if name starts with "_" , default addToInputs is false
	 * @param type type
	 * @return this
	 */
	public ProcedureBuilder appendArgs0InputValue(String name, IVariableType type) {
		return appendArgs0InputValue(name, type, name.charAt(0) != '_');
	}

	public ProcedureBuilder appendArgs0InputValueWithDefaultToolboxInit(String name, IVariableType type) {
		appendArgs0InputValue(name, type);
		type.initDefaultToolBox(this, name);
		return this;
	}

	public ProcedureBuilder appendArgs0InputValue(String name, IVariableType type, boolean addToInputs) {
		return appendArgs0InputValue(name, type.getBlocklyVariableType(), addToInputs);
	}

	/**
	 * { "type": "input_value", "name": "entity", "check": "Entity" }
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
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	public ProcedureBuilder appendArgs0FieldInput(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_input");
		jsonObject.addProperty("name", name);
		fields.add(name);
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	public ProcedureBuilder appendArgs0FieldInput(String name, String text) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_input");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("text", text);
		fields.add(name);
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * @param name name
	 * @return this
	 */
	public ProcedureBuilder appendArgs0StatementInput(String name) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "input_statement");
		jsonObject.addProperty("name", name);
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	public ProcedureBuilder appendArgs0StatementInput(String name, String... checksHigherName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "input_statement");
		jsonObject.addProperty("name", name);
		var array = new JsonArray();
		for (String check : checksHigherName) {
			array.add(check);
		}
		jsonObject.add("check", array);
		sequence.add(name);
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
		sequence.add(name);
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
		sequence.add(name);
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
		sequence.add(name);
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
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * @param src    resource url see {@link org.cdc.framework.utils.BuiltInImages}
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

	public ProcedureBuilder appendArgs0FieldDropDown(String name, String... map) {
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
		for (int index = 0; index < (map.length / 2); index++) {
			linkedHashMap.put(map[index * 2], map[index * 2 + 1]);
		}
		sequence.add(name);
		return appendArgs0FieldDropDown(name, linkedHashMap);
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
	 * @return this;
	 */
	public ProcedureBuilder appendArgs0FieldDropDown(String name, JsonElement... options) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_dropdown");
		jsonObject.addProperty("name", name);
		JsonArray options1 = new JsonArray();
		for (JsonElement jsonElement : options) {
			//如果只是普通的原始类型，则给他双倍后加入
			if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
				JsonArray jsonElements = new JsonArray();
				jsonElements.add(jsonPrimitive);
				jsonElements.add(jsonPrimitive);
				options1.add(jsonElements);
			} else {
				//其他的加进去就行了（
				options1.add(jsonElement);
			}
		}
		jsonObject.add("options", options1);
		fields.add(name);
		sequence.add(name);
		return appendArgs0Element(jsonObject);
	}

	/**
	 * {
	 * "type": "field_mcitem_selector",
	 * "name": "block",
	 * "supported_mcitems": "allblocks"
	 * },
	 *
	 * @param name name
	 * @return this
	 */
	public ProcedureBuilder appendArgs0FieldMCItemSelector(String name, String supported) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", "field_mcitem_selector");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("field_mcitem_selector", supported);
		fields.add(name);
		sequence.add(name);
		return this;
	}

	public ProcedureBuilder appendArgs0MultipleLinesField(String name,String content){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type","field_multilinetext");
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("text", content);
		fields.add(name);
		sequence.add(name);
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
	 * @param name      name
	 * @param lowerName type , should be lowerCase
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

	public ProcedureBuilder setPlaceHolderLanguage(LanguageBuilder languageBuilder, final String formmatted) {
		String result = formmatted;
		var list = sequence.stream().sorted(Comparator.comparingInt(String::length)).toList().reversed();
		for (String name : list) {
			result = result.replaceAll("%" + name, "%" + (sequence.indexOf(name) + 1));
		}
		return setLanguage(languageBuilder, result);
	}

	public ProcedureBuilder setMutator(String mutator){
		result.getAsJsonObject().addProperty("mutator",mutator);
		return this;
	}

	@CanIgnoreReturnValue public ProcedureBuilder setLanguage(LanguageBuilder languageBuilder, String value) {
		flags.flagToSetLang = true;
		if (isType) {
			languageBuilder.appendProcedureCategory(fileName.substring(1), value);
		} else {
			if (BuilderUtils.countLanguageParameterCount(value) != args0.size()) {
				throw new RuntimeException(
						"\" " + value + " \"is a irregular content because we need parameter count: " + args0.size());
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
		flags.flagToInitGenerator = true;
		return this;
	}

	private Function<Path, Boolean> generatorListener = (a) -> false;

	public ProcedureBuilder setGeneratorListener(Function<Path, Boolean> generatorListener) {
		this.generatorListener = generatorListener;
		return this;
	}

	public void initGenerator0(String generatorName, boolean replace) {
		if (isType) {
			return;
		}
		File generator = new File(rootPath, generatorName);
		File procedures = new File(generator, targetPath.getName());
		if (!procedures.exists()) {
			procedures.mkdirs();
		}
		try {
			Path template = new File(procedures, fileName + ".java.ftl").toPath();
			if (Files.exists(template) && !generatorListener.apply(template)) {
				String string = Files.readString(template);
				if (!string.startsWith("<#-- unchecked -->")) {
					inputs.asList().stream().map(a -> BuilderUtils.getInputPlaceHolder(a.getAsString())).forEach(a -> {
						if (!string.contains(a) && !string.contains(a.substring(2, a.length() - 1))) {
							System.err.println(template.toUri() + " :" + a + " is missing.");
						}
					});
					statements.asList().stream().map(a -> BuilderUtils.getStatementPlaceHolder(
							a.getAsJsonObject().get("name").getAsString())).forEach(a -> {
						if (!string.contains(a) && !string.contains(a.substring(2, a.length() - 1))) {
							System.err.println(template.toUri() + " :" + a + " is missing.");
						}
					});
					fields.asList().stream().map(a -> BuilderUtils.getFieldPlaceHolder(a.getAsString())).forEach(a -> {
						if (!string.contains(a) && !string.contains(a.substring(2, a.length() - 1))) {
							System.err.println(template.toUri() + " :" + a + " is missing.");
						}
					});
				}
				if (!string.contains("addTemplate??") && string.contains("@addTemplate file")) {
					System.err.println(
							template.toUri() + " : it will not compatible with 2024.4 because the addTemplate");
				}
			} else {
				String below2025 = """
						<#-- support 2025.1 below -->
						<#if addTemplate??>
						<#-- 2025.1 code -->
						<#else>
						</#if>
						""";
				String builder = BuilderUtils.generateInputsComment(inputs) + System.lineSeparator()
						+ BuilderUtils.generateStatementsComment(statements) + System.lineSeparator()
						+ BuilderUtils.generateFieldsComment(fields) + System.lineSeparator() + below2025;
				Files.copy(new ByteArrayInputStream(builder.getBytes(StandardCharsets.UTF_8)), template);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
		return flags.flagToInitGenerator && rootPath.equals(mCreatorPluginFactory.rootPath())
				&& BuilderUtils.isSupportProcedure(mCreatorPluginFactory.getCurrentInit());
	}

	@Override public JsonElement build() {
		if (isType) {
			return this.result;
		}

		if (!result.getAsJsonObject().has("inputsInline")) {
			setInputsInline(true);
		}
		//default color
		if (!result.getAsJsonObject().has(colorKey)) {
			setColor(ColorUtils.getSuggestColor(category));
		}
		if (!mcreator.has("group")) {
			setGroup("name");
		}
		if (!mcreator.has("toolbox_id")) {
			setToolBoxId(Objects.requireNonNullElse(category, BuiltInToolBoxId.Procedure.OTHER));
		}
		if (mcreator.has("toolbox_id")) {
			if (mcreator.get("toolbox_id").getAsString().equals(BuiltInToolBoxId.Procedure.OTHER)) {
				System.err.println("The " + fileName + " belong to others");
			}
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

	@Override public JsonElement buildAndOutput() throws IOException {
		if (!flags.flagToSetLang) {
			System.err.println("You should know the lang of " + fileName + " is not generated");
		}
		return super.buildAndOutput();
	}

	public class StatementBuilder extends JsonBuilder {

		protected final JsonArray provides;

		protected StatementBuilder() {
			super(null, null);
			this.result = new JsonObject();

			this.provides = new JsonArray();
		}

		/**
		 * @param name this should be the statementinput's name
		 * @return this
		 */
		public StatementBuilder setName(String name) {
			result.getAsJsonObject().addProperty("name", name);
			return this;
		}

		/**
		 * @param name      名称
		 * @param lowerName 类型,必须全部小写 type, must be the lowercase
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
			if (!this.result.getAsJsonObject().has("name")) {
				throw new RuntimeException("You should set the name of statement");
			}
			ProcedureBuilder.this.appendStatement(build());
			return ProcedureBuilder.this;
		}
	}

	public class ToolBoxInitBuilder {
		protected final String placeholder = "﨎";
		protected String result = placeholder;

		private boolean flagToSetName;

		public ToolBoxInitBuilder appendElement(String element) {
			result = result.replace(placeholder, element);
			return this;
		}

		public ToolBoxInitBuilder setName(String name) {
			flagToSetName = true;
			return appendElement("<value name=\"" + name + "\">" + placeholder + "</value>");
		}

		public ToolBoxInitBuilder appendDependencyDirection() {
			return appendElement("<block type=\"direction_from_deps\"/>");
		}

		public ToolBoxInitBuilder appendDependencyBlockState() {
			return appendElement("<block type=\"blockstate_from_deps\"/>");
		}

		public ToolBoxInitBuilder appendDependencyDamageSource() {
			return appendElement("<block type=\"damagesource_from_deps\"/>");
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

		public ToolBoxInitBuilder appendNumberField(int num) {
			return appendElement("<field name=\"NUM\">" + num + "</field>");
		}

		public ToolBoxInitBuilder appendConstantString(String str) {
			return appendElement("<block type=\"text\"><field name=\"TEXT\">" + str + "</field></block>");
		}

		public ToolBoxInitBuilder appendReferenceBlock(String type) {
			return appendElement("<block type=\"" + type + "\">﨎</block>");
		}

		public ToolBoxInitBuilder appendConstantBoolean(boolean bool) {
			var str = String.valueOf(bool).toUpperCase();
			return appendElement("<block type=\"logic_boolean\"><field name=\"BOOL\">" + str + "</field></block>");
		}

		public ToolBoxInitBuilder appendPlaceHolder(String name) {
			return appendElement(
					"<block deletable=\"false\" movable=\"false\" enabled=\"false\" type=\"" + name + "\"></block>");
		}

		public ToolBoxInitBuilder appendEntityIterator() {
			return appendPlaceHolder("entity_iterator");
		}

		public ProcedureBuilder buildAndReturn() {
			if (!flagToSetName) {
				System.err.println("Empty name of toolboxinit");
			}
			result = result.replace(placeholder, "");
			return ProcedureBuilder.this.appendToolBoxInit(result);
		}
	}
}
