package org.cdc.framework.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import org.cdc.framework.utils.ColorUtils;

import java.awt.*;
import java.io.File;

public class ProcedureBuilder extends JsonBuilder {
    private boolean isType;

    private String colorKey;

    private final JsonObject mcreator;
    private final JsonArray inputs;
    private final JsonArray fields;
    private final JsonArray statements;
    private final JsonArray args0;
    private final JsonArray dependencies;
    private final JsonArray extensions;

    public ProcedureBuilder(File rootPath){
        this(rootPath,"procedures");
    }

    public ProcedureBuilder(File rootPath,String child) {
        super(rootPath, new File(rootPath, child));

        this.colorKey = "colour";

        this.result = new JsonObject();

        this.mcreator = new JsonObject();
        this.inputs = new JsonArray();
        this.fields = new JsonArray();
        this.statements = new JsonArray();
        this.dependencies = new JsonArray();
        this.extensions = new JsonArray();

        this.args0 = new JsonArray();
    }

    public ProcedureBuilder setName(String name) {
        this.fileName = name;
        return this;
    }

    public ProcedureBuilder markType() {
        colorKey = "color";
        isType = true;
        result.getAsJsonObject().remove("mcreator");
        if (!fileName.startsWith("$")) {
            this.fileName = "$" + this.fileName;
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

    public ProcedureBuilder setGroup(String group) {
        mcreator.addProperty("group", group);
        return this;
    }

    public ProcedureBuilder setToolBoxId(String toolBoxId) {
        mcreator.addProperty("toolbox_id", toolBoxId);
        return this;
    }

    public ProcedureBuilder appendToolBoxInit(String init) {
        if (!mcreator.has("toolbox_init"))
            mcreator.add("toolbox_init", new JsonArray());
        mcreator.get("toolbox_init").getAsJsonArray().add(init);
        return this;
    }

    public ProcedureBuilder appendArgs0Element(JsonElement jsonElement) {
        args0.add(jsonElement);
        return this;
    }

    public ProcedureBuilder appendArgs0InputValue(String name, String lowerName) {
        return appendArgs0InputValue(name, lowerName, true);
    }

    /**
     * {
     * "type": "input_value",
     * "name": "entity",
     * "check": "Entity"
     * }
     *
     * @param name        名字
     * @param higherName       这个是检查类型,必须是Object这样的
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

    public ProcedureBuilder appendArgs0StatementInput(String name) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "input_statement");
        jsonObject.addProperty("name", name);
        return appendArgs0Element(jsonObject);
    }

    /**
     * {
     * "type": "field_checkbox",
     * "name": "insight",
     * "checked": false
     * },
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
     * {
     * "type": "field_data_list_selector",
     * "name": "entity",
     * "datalist": "entity",
     * "testValue": "EntityCreeper"
     * },
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
     *     {
     *       "type": "field_ai_condition_selector",
     *       "name": "condition"
     *     }
     * @param name 名字
     * @return this
     */
    public ProcedureBuilder appendArgs0FieldAIConditionSelector(String name){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "field_ai_condition_selector");
        jsonObject.addProperty("name", name);
        fields.add(name);
        return appendArgs0Element(jsonObject);
    }

    /**
     *
     * @param jsonObject element
     * @return this
     * @apiNote append
     */
    @CanIgnoreReturnValue
    public ProcedureBuilder appendStatement(JsonElement jsonObject) {
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
     * "dependencies": [
     * {
     * "name": "world",
     * "type": "world"
     * }
     * ]
     *
     * @param jsonObject json
     * @return this
     */
    public ProcedureBuilder appendDependency(JsonObject jsonObject) {
        dependencies.add(jsonObject);
        return this;
    }

    /**
     * "dependencies": [
     * {
     * "name": "world",
     * "type": "world"
     * }
     * ]
     *
     * @param name 名称
     * @param lowerName 类型,比如全部小写
     * @return this
     */
    public ProcedureBuilder appendDependency(String name, String lowerName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("type", lowerName.toLowerCase());
        return appendDependency(jsonObject);
    }

    public ProcedureBuilder setLanguage(LanguageBuilder languageBuilder, String value) {
        if (isType)
            languageBuilder.appendProcedureCategory(fileName,value);
        else
            languageBuilder.appendProcedure(fileName, value);
        return this;
    }

    @Override
    JsonElement build() {
        if (!args0.isEmpty()) {
            this.result.getAsJsonObject().add("args0", args0);
        }
        if (!extensions.isEmpty()) {
            this.result.getAsJsonObject().add("extensions", extensions);
        }
        if (!mcreator.isEmpty()) {
            this.result.getAsJsonObject().add("mcreator", mcreator);
        }
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
         * @param name 名称
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

        @Override
        JsonElement build() {
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
}
