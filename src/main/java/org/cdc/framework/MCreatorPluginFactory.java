package org.cdc.framework;

import org.cdc.framework.builder.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public record MCreatorPluginFactory(File rootPath) {

    public static final ArrayList<IGeneratorInit> generatorInits = new ArrayList<>();

    public static MCreatorPluginFactory createFactory(String folder) {
        return new MCreatorPluginFactory(new File(folder));
    }

    public void createFolder(String name) {
        var file = new File(rootPath, name);
        file.mkdirs();
    }

    public void initGenerator(String generator) {
        createFolder(generator);
        var generator1 = new File(rootPath, generator);
        var file = new File(generator1, "aitasks");
        file.mkdirs();

        file = new File(generator1, "mappings");
        file.mkdirs();

        file = new File(generator1, "procedures");
        file.mkdirs();

        file = new File(generator1, "triggers");
        file.mkdirs();

        file = new File(generator1, "variables");
        file.mkdirs();

        generatorInits.forEach(a -> {
            if (a.isSupported(this))
                a.initGenerator0(generator);
        });
    }

    public ProcedureBuilder createProcedure() {
        createFolder("procedures");
        return new ProcedureBuilder(rootPath);
    }

    public AITasksBuilder createAITask() {
        createFolder("aitasks");
        return new AITasksBuilder(rootPath);
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
