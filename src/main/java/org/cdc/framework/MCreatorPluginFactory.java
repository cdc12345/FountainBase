package org.cdc.framework;

import org.cdc.framework.builder.*;
import org.cdc.framework.utils.MCreatorVersions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

public class MCreatorPluginFactory {

    public static final ArrayList<IGeneratorInit> generatorInits = new ArrayList<>();

    public static MCreatorPluginFactory createFactory(String folder) {
        return new MCreatorPluginFactory(new File(folder));
    }

    private final File rootPath;
    private String version;

    public MCreatorPluginFactory(File rootPath){
        this.rootPath = rootPath;
        this.version = MCreatorVersions.V_2025_1;
    }

    public void createFolder(String name) {
        var file = new File(rootPath, name);
        file.mkdirs();
    }

    public void setVersion(String version){
        this.version = version;
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
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".ProcedureBuilder");
            return (ProcedureBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                                                          IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new ProcedureBuilder(rootPath);
    }

    public AITasksBuilder createAITask() {
        createFolder("aitasks");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".AITasksBuilder");
            return (AITasksBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new AITasksBuilder(rootPath);
    }

    public VariableBuilder createVariable() {
        createFolder("variables");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".VariableBuilder");
            return (VariableBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new VariableBuilder(rootPath);
    }

    public LanguageBuilder createDefaultLanguage() {
        createFolder("lang");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".LanguageBuilder");
            return (LanguageBuilder) class1.getConstructor(new Class[]{File.class,String.class}).newInstance(rootPath,"texts");
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new LanguageBuilder(rootPath, "texts");
    }

    public LanguageBuilder createLanguage(Locale locale) {
        createFolder("lang");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".LanguageBuilder");
            return (LanguageBuilder) class1.getConstructor(new Class[]{File.class,String.class}).newInstance(rootPath,"texts_" + locale.getLanguage() + "_" + locale.getCountry());
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new LanguageBuilder(rootPath, "texts_" + locale.getLanguage() + "_" + locale.getCountry());
    }

    public DataListBuilder createDataList() {
        createFolder("datalists");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".DataListBuilder");
            return (DataListBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new DataListBuilder(rootPath);
    }

    public TriggerBuilder createTrigger() {
        createFolder("triggers");
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".TriggerBuilder");
            return (TriggerBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new TriggerBuilder(rootPath);
    }

    public void createApis(String apiName){
        createFolder("apis");
        try {
            Files.copy(new ByteArrayInputStream(("name: "+apiName).getBytes(StandardCharsets.UTF_8)), Path.of(rootPath.getPath(),"apis",apiName+".yaml"));
        } catch (IOException ignored) {
        }
    }

    public File rootPath() {
        return rootPath;
    }
}
