package org.cdc.framework.utils;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.ProcedureBuilder;
import org.cdc.framework.interfaces.IProcedureCategory;

public class BuilderUtils {
    public static ProcedureBuilder createOutputProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name, String output) {
        return mCreatorPluginFactory.createProcedure().setName(name).setOutput(output).setGroup("name").setInputsInline(true);
    }

    public static ProcedureBuilder createCommonProcedure(MCreatorPluginFactory mCreatorPluginFactory,String name){
        return mCreatorPluginFactory.createProcedure().setName(name).setPreviousStatement(null).setNextStatement(null).setInputsInline(true);
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

    public static boolean isSupportProcedure(String generatorName){
        return generatorName.startsWith("forge") || generatorName.startsWith("neoforge");
    }
}
