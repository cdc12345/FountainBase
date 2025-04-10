package org.cdc.framework.utils;

import com.google.gson.JsonArray;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.ProcedureBuilder;
import org.cdc.framework.interfaces.IProcedureCategory;
import org.cdc.framework.interfaces.IVariableType;

import java.util.stream.Collectors;

public class BuilderUtils {

    public static ProcedureBuilder createOutputProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name, String output) {
        return mCreatorPluginFactory.createProcedure(name).setOutput(output).setGroup("name").setInputsInline(true);
    }

    /**
     * mCreatorPluginFactory.createProcedure(name).setOutput(variableType).setGroup("name").setInputsInline(true)
     */
    public static ProcedureBuilder createOutputProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name, IVariableType variableType) {
        return mCreatorPluginFactory.createProcedure(name).setOutput(variableType).setGroup("name").setInputsInline(true);
    }

    /**
     * .createProcedure(name).setGroup("name").setPreviousStatement(null).setNextStatement(null).setInputsInline(true)
     */
    public static ProcedureBuilder createCommonProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name) {
        return mCreatorPluginFactory.createProcedure(name).setGroup("name").setPreviousStatement(null).setNextStatement(null).setInputsInline(true);
    }

    public static ProcedureBuilder createProcedureCategory(MCreatorPluginFactory mCreatorPluginFactory, IProcedureCategory category) {
        var aitaskscat = mCreatorPluginFactory.createProcedure();
        aitaskscat.markType();
        if (category != null) {
            aitaskscat.setName(category.getName());
        }
        return aitaskscat;
    }

    public static ProcedureBuilder createAITaskCategory(MCreatorPluginFactory mCreatorPluginFactory, IProcedureCategory category) {
        var pro = mCreatorPluginFactory.createAITask();
        pro.markType();
        if (category != null) {
            pro.setName(category.getName());
        }
        return pro;
    }

    public static boolean isSupportProcedure(String generatorName) {
        return generatorName.startsWith("forge") || generatorName.startsWith("neoforge") || generatorName.startsWith("fabric");
    }

    public static String generateInputsComment(JsonArray inputs) {
        return inputs.asList().stream().map(a -> "${input$" + a.getAsString() + "}").collect(Collectors.joining(",", "<#-", "->"));
    }

    public static String generateStatementsComment(JsonArray statements){
        return statements.asList().stream().map(a -> "${statement$" + a.getAsString() + "}").collect(Collectors.joining(",", "<#-", "->"));
    }

    public static String generateFieldsComment(JsonArray fields){
        return fields.asList().stream().map(a -> "${field$" + a.getAsString() + "}").collect(Collectors.joining(",", "<#-", "->"));
    }
}
