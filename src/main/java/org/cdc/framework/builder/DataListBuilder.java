package org.cdc.framework.builder;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class DataListBuilder extends FileOutputBuilder<ArrayList<String>> implements IGeneratorInit {
    private final ArrayList<String> result;
    public DataListBuilder(File rootPath) {
        super(rootPath, new File(rootPath,"datalists"));
        result =new ArrayList<>();
        this.fileExtension = "yaml";
    }

    public DataListBuilder setName(String name){
        this.fileName = name;
        return this;
    }

    public DataListBuilder appendElement(String element){
        result.add(element);
        return this;
    }

    @Override
    public ArrayList<String> build() {
        return result;
    }

    @Override
    public ArrayList<String> buildAndOutput() {
        var build = build();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("- ").append(build.getFirst());
            build.stream().skip(1).forEach(a-> stringBuilder.append(System.lineSeparator()).append("- ").append(a));
            Files.copy(new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)),new File(targetPath,getFileFullName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return build;
    }

    public DataListBuilder initGenerator(){
        MCreatorPluginFactory.generatorInits.add(this);
        return this;
    }

    @Override
    public void initGenerator0(String generatorName) {
        HashMap<String,String> hashMap = new HashMap<>();
        for (String name:result){
            hashMap.put(name,name);
        }
        var generator1 = Paths.get(rootPath.getPath(),generatorName,"mappings",getFileFullName());
        try {
            Files.copy(new ByteArrayInputStream(hashMap.toString().replace("{","").
                    replace("}","").replace("=",": ").
                    replace(", ",System.lineSeparator()).getBytes(StandardCharsets.UTF_8)),generator1);
        } catch (IOException ignored) {}
    }

    @Override
    public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
        return mCreatorPluginFactory.rootPath().equals(rootPath);
    }
}
