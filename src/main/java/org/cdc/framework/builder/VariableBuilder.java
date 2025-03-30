package org.cdc.framework.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cdc.framework.utils.ColorUtils;

import java.awt.*;
import java.io.File;

public class VariableBuilder extends JsonBuilder{

    public VariableBuilder(File rootPath) {
        super(rootPath, new File(rootPath,"variables"));
        this.result = new JsonObject();
    }

    public VariableBuilder setName(String name){
        this.fileName = name;
        return this;
    }

    public VariableBuilder setColor(int color){
        result.getAsJsonObject().add("color",new JsonPrimitive(color));
        return this;
    }

    public VariableBuilder setColor(String color){
        result.getAsJsonObject().add("color",new JsonPrimitive(color));
        return this;
    }

    public VariableBuilder setColor(Color color){
        return setColor(ColorUtils.toHex(color));
    }

    public VariableBuilder setBlocklyVariableType(String type){
        result.getAsJsonObject().addProperty("blocklyVariableType",type);
        return this;
    }

    public VariableBuilder setNullable(boolean nullable){
        result.getAsJsonObject().addProperty("nullable",nullable);
        return this;
    }

    public VariableBuilder setIgnoredByCoverage(boolean ignoredByCoverage){
        result.getAsJsonObject().addProperty("ignoredByCoverage",ignoredByCoverage);
        return this;
    }

    @Override
    JsonElement build() {
        return result;
    }
}
