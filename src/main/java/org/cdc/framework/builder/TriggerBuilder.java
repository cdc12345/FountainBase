package org.cdc.framework.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cdc.framework.utils.Side;

import java.io.File;

public class TriggerBuilder extends JsonBuilder{

    private final JsonArray dependencies;

    public TriggerBuilder(File rootPath) {
        super(rootPath, new File(rootPath,"triggers"));

        this.result = new JsonObject();
        this.dependencies = new JsonArray();
        this.result.getAsJsonObject().add("dependencies_provided",dependencies);
    }

    public TriggerBuilder setName(String name){
        this.fileName = name;
        return this;
    }

    public TriggerBuilder setHasResult(boolean hasResult){
        this.result.getAsJsonObject().addProperty("has_result",hasResult);
        return this;
    }

    public TriggerBuilder setCancelable(boolean cancelable){
        this.result.getAsJsonObject().addProperty("cancelable",cancelable);
        return this;
    }

    public TriggerBuilder setSide(Side side){
        this.result.getAsJsonObject().addProperty("side",side.name().toLowerCase());
        return this;
    }

    /**
     *     {
     *       "name": "z",
     *       "type": "number"
     *     }
     * @param name 依赖名称
     * @param type 依赖类型.类似于number
     * @return this
     */
    public TriggerBuilder appendDependency(String name,String type){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",name);
        jsonObject.addProperty("type",type);
        dependencies.add(jsonObject);
        return this;
    }

    public TriggerBuilder setLanguage(LanguageBuilder languageBuilder,String content){
        languageBuilder.appendTrigger(fileName,content);
        return this;
    }

    @Override
    JsonElement build() {
        return result;
    }
}
