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
import java.util.HashMap;
import java.util.Map;

public class DataListBuilder extends FileOutputBuilder<Map<String,String>> implements IGeneratorInit {
    private final Map<String,String> result;
    public DataListBuilder(File rootPath) {
        super(rootPath, new File(rootPath,"datalists"));
        result = new HashMap<>();
        this.fileExtension = "yaml";
    }

    public DataListBuilder setName(String name){
        this.fileName = name;
        return this;
    }

    public DataListBuilder appendElement(String element) {
        return appendElement(element,element);
    }

    public DataListBuilder appendElement(String element,String defaultMapping){
        result.put(element, defaultMapping);
        return this;
    }

    @Override
    public Map<String,String> build() {
        return result;
    }

    @Override
    public Map<String,String> buildAndOutput() {
        if (fileName == null){
            throw new RuntimeException("filename can not be null");
        }
        var build1 = build();
        try {
            var build = build1.keySet().stream().toList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("- ").append(build.getFirst());
            build.stream().skip(1).forEach(a-> stringBuilder.append(System.lineSeparator()).append("- ").append(a));
            Files.copy(new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)),new File(targetPath,getFileFullName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return build1;
    }

    public DataListBuilder initGenerator(){
        MCreatorPluginFactory.generatorInits.add(this);
        return this;
    }

    @Override
    public void initGenerator0(String generatorName) {
        if (fileName == null){
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>(result);
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
