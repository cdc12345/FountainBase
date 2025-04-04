package org.cdc.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public static String tryGenerateProcedureBuilderCode(File file){
        Gson gson = new Gson();
        var js = gson.fromJson(loadStringFromFile(file), JsonObject.class);
        StringBuilder procedure = new StringBuilder();
        procedure.append("mcr.createProcedure(\"").append(getFileName(file)).append("\")");
        if (js.has("inputsInline")){
            procedure.append(".setInputsInline(").append(js.get("inputsInline").getAsBoolean()).append(")");
        }
        if (js.has("colour")){
            procedure.append(".setColor(").append(js.get("colour")).append(")");
        }
        if (js.has("output")){
            procedure.append(".setOutput(").append(js.get("output")).append(")");
        }
        if (js.has("previousStatement")){
            procedure.append(".setPreviousStatement(");
            if (!js.get("previousStatement").isJsonNull()){
                procedure.append("\"");
                procedure.append(js.get("previousStatement").getAsString());
                procedure.append("\"");
            } else {
                procedure.append("null");
            }
            procedure.append(")");
        }
        if (js.has("nextStatement")){
            procedure.append(".setNextStatement(");
            if (!js.get("previousStatement").isJsonNull()){
                procedure.append("\"");
                procedure.append(js.get("nextStatement").getAsString());
                procedure.append("\"");
            } else {
                procedure.append("null");
            }
            procedure.append(")");
        }
        if (js.has("mcreator")){
            JsonObject mcreator = js.getAsJsonObject("mcreator");
            if (mcreator.has("toolbox_id")){
                procedure.append(".setToolBoxId(\"").append(mcreator.get("toolbox_id").getAsString()).append("\")");
            }
            if (mcreator.has("dependencies")){
                JsonArray dependencies = mcreator.getAsJsonArray("dependencies");
                for (JsonElement jsonElement:dependencies){
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    procedure.append(".appendDependency(").append(jsonObject.get("name")).append(",").append(jsonObject.get("type")).append(")");
                }
            }
            if (mcreator.has("group")){
                procedure.append(".setGroup(").append(mcreator.get("group")).append(")");
            }
        }
        return procedure.toString();
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
        return builder.toString();
    }

    public static String getFileName(File file){
        return file.getName().split("\\.")[0];
    }
}
