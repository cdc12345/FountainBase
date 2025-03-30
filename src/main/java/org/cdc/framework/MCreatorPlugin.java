package org.cdc.framework;

import org.cdc.framework.builder.*;

import java.io.File;
import java.util.Locale;

public record MCreatorPlugin(File rootPath) {

    public void createFolder(String name) {
        var file = new File(rootPath, name);
        file.mkdirs();
    }

    public void initGenerator(String generator){
        createFolder(generator);
        var generator1 = new File(rootPath,generator);
        if (new File(rootPath,"aitasks").exists()){
            var aitasks1 = new File(generator1,"aitasks");
            aitasks1.mkdirs();
        }

        if (new File(rootPath,"datalists").exists()){
            var file = new File(generator1,"mappings");
            file.mkdirs();
        }

        if (new File(rootPath,"procedures").exists()){
            var file = new File(generator1,"procedures");
            file.mkdirs();
        }

        if (new File(rootPath,"triggers").exists()){
            var file = new File(generator1,"triggers");
            file.mkdirs();
        }

        if (new File(rootPath,"variables").exists()){
            var file = new File(generator1,"variables");
            file.mkdirs();
        }
    }

    public ProcedureBuilder createProcedure() {
        createFolder("procedures");
        return new ProcedureBuilder(rootPath);
    }

    public ProcedureBuilder createAITask() {
        createFolder("aitasks");
        return new ProcedureBuilder(rootPath, "aitasks");
    }

    public VariableBuilder createVariable() {
        createFolder("variables");
        return new VariableBuilder(rootPath);
    }

    public LanguageBuilder createDefaultLanguage() {
        createFolder("lang");
        return new LanguageBuilder(rootPath, "texts");
    }

    public LanguageBuilder createLanguage(Locale locale) {
        createFolder("lang");
        return new LanguageBuilder(rootPath, "texts_" + locale.getLanguage() + "_" + locale.getCountry());
    }

    public DataListBuilder createDataList() {
        createFolder("datalists");
        return new DataListBuilder(rootPath);
    }

    public TriggerBuilder createTrigger() {
        createFolder("triggers");
        return new TriggerBuilder(rootPath);
    }
}
