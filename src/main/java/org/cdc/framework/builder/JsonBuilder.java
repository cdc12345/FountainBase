package org.cdc.framework.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class JsonBuilder extends FileOutputBuilder<JsonElement>{
    protected JsonElement result;

    protected JsonBuilder(File rootPath,File targetPath){
        super(rootPath,targetPath);
        this.fileExtension = "json";
    }

    @Override
    public JsonElement buildAndOutput() {
        var json = build();
        if (targetPath != null) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
                Files.copy(new ByteArrayInputStream(gson.toJson(json).getBytes(StandardCharsets.UTF_8)),new File(targetPath,getFileFullName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return json;
    }
}
