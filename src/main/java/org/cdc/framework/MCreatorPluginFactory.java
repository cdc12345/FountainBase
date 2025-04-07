package org.cdc.framework;

import org.cdc.framework.builder.*;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.interfaces.IProcedureCategory;
import org.cdc.framework.interfaces.IVariableType;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.FileUtils;
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

    private String currentInit;

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
        initGenerator(generator,false);
    }

    public void initGenerator(String generator,boolean replace) {
        createFolder(generator);
        var generator1 = new File(rootPath, generator);
        var file = new File(generator1, "aitasks");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        file = new File(generator1, "mappings");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        file = new File(generator1, "procedures");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        file = new File(generator1, "triggers");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        file = new File(generator1, "variables");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        file = new File(generator1,"templates");
        if (replace){
            FileUtils.deleteNonEmptyDirector(file);
        }
        file.mkdirs();

        currentInit = generator;
        generatorInits.forEach(a -> {
            if (a.isSupported(this))
                a.initGenerator0(generator);
        });
        currentInit = null;
    }

    public ProcedureBuilder createProcedure(){
        return createProcedure(null);
    }

    public ProcedureBuilder createProcedure(String name) {
        createFolder("procedures");
        ProcedureBuilder builder;
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".ProcedureBuilder");
            builder = (ProcedureBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                                                          IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        builder = new ProcedureBuilder(rootPath);
        if (name != null){
            builder.setName(name);
        }
        return builder;
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

    public VariableBuilder createVariable(){
        return createVariable(null);
    }

    public VariableBuilder createVariable(IVariableType type) {
        createFolder("variables");
        VariableBuilder builder;
        try {
            var class1 = Class.forName("org.cdc.framework.builder."+version+".VariableBuilder");
            builder = (VariableBuilder) class1.getConstructor(new Class[]{File.class}).newInstance(rootPath);
        } catch (ClassNotFoundException ignored){

        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        builder = new VariableBuilder(rootPath);
        if (type != null){
            builder.setName(type.getVariableType()).setBlocklyVariableType(type.getBlocklyVariableType());
        }
        return builder;
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

    public ProcedureBuilder createProcedureCategory(){
        return BuilderUtils.createProcedureCategory(this,null);
    }

    public ProcedureBuilder createAITaskCategory(){
        return BuilderUtils.createAITaskCategory(this,null);
    }

    public File rootPath() {
        return rootPath;
    }

    public String getCurrentInit() {
        return currentInit;
    }

    public ToolKit getToolKit(){
        return new ToolKit();
    }

    public class ToolKit{
        private ToolKit(){
        }

        public ProcedureBuilder createInputProcedure(String name){
            return BuilderUtils.createCommonProcedure(MCreatorPluginFactory.this, name);
        }

        public ProcedureBuilder createOutputProcedure(String name,String output){
            return BuilderUtils.createOutputProcedure(MCreatorPluginFactory.this,name,output);
        }

        public ProcedureBuilder createOutputProcedure(String name,IVariableType output){
            return BuilderUtils.createOutputProcedure(MCreatorPluginFactory.this,name,output);
        }

        public String getCurrentInitGenerator(){
            return MCreatorPluginFactory.this.currentInit;
        }
    }
}
