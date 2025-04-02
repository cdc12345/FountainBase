package org.cdc.framework.builder;

import com.google.errorprone.annotations.DoNotCall;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.ColorUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VariableBuilder extends JsonBuilder implements IGeneratorInit {

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

    /**
     * this is not implement in 2025.1
     *
     * @param name name
     * @return this
     */
    @DoNotCall("this version not supported")
    public VariableBuilder appendRequiredApi(String name){
        return this;
    }

    @Override
    public JsonElement build() {
        return result;
    }

    public VariableBuilder initGenerator(){
        MCreatorPluginFactory.generatorInits.add(this);
        return this;
    }

    @Override
    public void initGenerator0(String generatorName) {
        var generator1 = Paths.get(rootPath.getPath(),generatorName,targetPath.getName(),fileName + ".yaml");
        try {
            Files.copy(new ByteArrayInputStream("#AutoGenerated".getBytes(StandardCharsets.UTF_8)),generator1);
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
        return mCreatorPluginFactory.rootPath().equals(rootPath) && BuilderUtils.isSupportProcedure(mCreatorPluginFactory.getCurrentInit());
    }
}
