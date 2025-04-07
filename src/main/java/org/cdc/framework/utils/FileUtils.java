package org.cdc.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.management.DescriptorKey;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {
    public static void deleteNonEmptyDirector(File directory) {
        try {
            //列出数组中的所有文件
            File[] files = directory.listFiles();

            //从目录中删除每个文件
            for (File file : files) {
                System.out.println(file + " deleted.");
                file.delete();
            }

            //删除目录
            if (directory.delete()) {
                System.out.println("目录已删除");
            } else {
                System.out.println("Directory not Found");
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static String loadStringFromFile(File file){
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonArray loadInputsFromFile(File file){
        Gson gson = new Gson();
        var js = gson.fromJson(loadStringFromFile(file), JsonObject.class);
        return gson.fromJson(js.get("mcreator").getAsJsonObject().get("inputs"), JsonArray.class);
    }

    @DescriptorKey("incomplete")
    public static String tryGenerateProcedureBuilderCode(File fl){
        Gson gson = new Gson();
        var file = gson.fromJson(loadStringFromFile(fl), JsonObject.class);
        StringBuilder builder = new StringBuilder();
        builder.append("mcr.createProcedure(\"").append(getFileName(fl)).append("\")");
        if (file.has("extensions")){
            JsonArray jsonArray = file.getAsJsonArray("extensions");
            for (JsonElement jsonElement:jsonArray){
                builder.append(".appendExtension(").append(jsonElement.toString()).append(')');
            }
        }
        if (file.has("inputsInline")){
            builder.append(".setInputsInline(").append(file.get("inputsInline").getAsBoolean()).append(")");
        }
        if (file.has("colour")){
            builder.append(".setColor(").append(file.get("colour")).append(")");
        }
        if (file.has("output")){
            JsonElement type = file.get("output");
            BuiltInTypes builtInTypes = BuiltInTypes.getType(type.getAsString());
            String type1 = (builtInTypes == null)?type.toString():"BuiltInTypes."+builtInTypes.name();
            builder.append(".setOutput(").append(type1).append(")");
        }
        if (file.has("previousStatement")){
            builder.append(".setPreviousStatement(");
            if (!file.get("previousStatement").isJsonNull()){
                builder.append("\"");
                builder.append(file.get("previousStatement").getAsString());
                builder.append("\"");
            } else {
                builder.append("null");
            }
            builder.append(")");
        }
        if (file.has("nextStatement")){
            builder.append(".setNextStatement(");
            if (!file.get("previousStatement").isJsonNull()){
                builder.append("\"");
                builder.append(file.get("nextStatement").getAsString());
                builder.append("\"");
            } else {
                builder.append("null");
            }
            builder.append(")");
        }
        if (file.has("mcreator")){
            JsonObject mcreator = file.getAsJsonObject("mcreator");
            if (mcreator.has("toolbox_id")){
                builder.append(".setToolBoxId(").append(mcreator.get("toolbox_id")).append(")");
            }
            if (mcreator.has("dependencies")){
                JsonArray dependencies = mcreator.getAsJsonArray("dependencies");
                for (JsonElement jsonElement:dependencies){
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement type = jsonObject.get("type");
                    BuiltInTypes builtInTypes = BuiltInTypes.getType(type.getAsString());
                    String type1 = (builtInTypes == null)?type.toString():"BuiltInTypes."+builtInTypes.name();
                    builder.append(".appendDependency(").append(jsonObject.get("name")).append(",").append(type1).append(")");
                }
            }
            if (mcreator.has("group")){
                builder.append(".setGroup(").append(mcreator.get("group")).append(")");
            }
            if (mcreator.has("required_api")){
                JsonArray jsonArray = mcreator.getAsJsonArray("required_api");
                for (JsonElement jsonElement:jsonArray){
                    builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
                }
            }
        }
        return builder.toString();
    }

    public static String tryGenerateVariableCode(File fl){
        Gson gson = new Gson();
        var file = gson.fromJson(loadStringFromFile(fl), JsonObject.class);
        StringBuilder builder = new StringBuilder("mcr.createVariable().setName(\""+getFileName(fl)+"\")");
        if (file.has("color")){
            builder.append(".setColor(").append(file.get("color")).append(")");
        }
        if (file.has("blocklyVariableType")){
            builder.append(".setBlocklyVariableType(").append(file.get("blocklyVariableType")).append(")");
        }
        if (file.has("nullable")){
            builder.append(".setNullable(").append(file.get("nullable")).append(")");
        }
        if (file.has("ignoredByCoverage")){
            builder.append(".setIgnoredByCoverage(").append(file.get("ignoredByCoverage")).append(")");
        }
        if (file.has("required_api")){
            JsonArray jsonArray = file.getAsJsonArray("required_api");
            for (JsonElement jsonElement:jsonArray){
                builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
            }
        }
        return builder.toString();
    }

    public static String tryGenerateTrigger(File fl){
        Gson gson = new Gson();
        var file = gson.fromJson(loadStringFromFile(fl), JsonObject.class);
        StringBuilder builder = new StringBuilder("mcr.createTrigger().setName(\""+getFileName(fl)+"\")");
        if (file.has("side")){
            builder.append(".setSide(Side.").append(Side.getSide(file.get("side").getAsString()).name()).append(")");
        }
        if (file.has("cancelable")){
            builder.append(".setCancelable(").append(Boolean.parseBoolean(file.get("cancelable").getAsString())).append(")");
        }
        if (file.has("has_result")){
            builder.append(".setHasResult(").append(Boolean.parseBoolean(file.get("has_result").getAsString())).append(")");
        }
        if (file.has("required_api")){
            JsonArray jsonArray = file.getAsJsonArray("required_api");
            for (JsonElement jsonElement:jsonArray){
                builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
            }
        }
        if (file.has("dependencies_provided")){
            JsonArray jsonArray = file.getAsJsonArray("dependencies_provided");
            for (JsonElement jsonElement: jsonArray){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement type = jsonObject.get("type");
                BuiltInTypes builtInTypes = BuiltInTypes.getType(type.getAsString());
                String type1 = (builtInTypes == null)?type.toString():"BuiltInTypes."+builtInTypes.name();
                builder.append(".appendDependency(").append(jsonObject.get("name")).append(",").append(type1).append(')');
            }
        }
        return builder.toString();
    }

    public static String getFileName(File file){
        return file.getName().split("\\.")[0];
    }
}
