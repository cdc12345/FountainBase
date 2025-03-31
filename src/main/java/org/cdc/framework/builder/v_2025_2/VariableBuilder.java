package org.cdc.framework.builder.v_2025_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;

public class VariableBuilder extends org.cdc.framework.builder.VariableBuilder {
    private final JsonArray requiredApis;

    public VariableBuilder(File rootPath) {
        super(rootPath);
        this.requiredApis = new JsonArray();
    }

    @Override
    public org.cdc.framework.builder.VariableBuilder appendRequiredApi(String name) {
        requiredApis.add(name);
        return super.appendRequiredApi(name);
    }

    @Override
    public JsonElement build(){
        this.result.getAsJsonObject().add("required_apis",requiredApis);
        return super.build();
    }
}
