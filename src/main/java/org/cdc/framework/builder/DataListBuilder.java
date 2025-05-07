package org.cdc.framework.builder;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataListBuilder extends FileOutputBuilder<Map<String,String>> implements IGeneratorInit {
    private final String DEFAULT_KEY = "_default";

	private final Map<String,String> result;
    public DataListBuilder(File rootPath) {
        super(rootPath, new File(rootPath,"datalists"));
        result = new LinkedHashMap<>();
        this.fileExtension = "yaml";
    }

    public DataListBuilder setName(String name){
        this.fileName = FileUtils.filterSpace(name);
        return this;
    }

    public DataListBuilder appendElement(String element) {
        return appendElement(element,element);
    }

    public DataListBuilder appendElement(String element,String defaultMapping){
        result.put(element, defaultMapping);
        return this;
    }

    public DataListBuilder setDefault(){
        return setDefault(result.values().stream().findFirst().orElse(null));
    }

    public DataListBuilder setDefault(String defaultMapping){
        return appendElement(DEFAULT_KEY,defaultMapping);
    }

    public DataListBuilder setMapTemplate(String mapTemplate){
		String MAP_TEMPLATE = "_mcreator_map_template";
		return appendElement(MAP_TEMPLATE,mapTemplate);
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
            var build = build1.keySet().stream().filter(a->!a.equals(DEFAULT_KEY)).toList();
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
    public void initGenerator0(String generatorName ,boolean replace) {
        if (fileName == null){
            return;
        }
        TreeMap<String, String> hashMap = new TreeMap<>();
        for (Map.Entry<String,String> entry:result.entrySet()){
            if (entry.getKey().contains(": ")){
                hashMap.put(entry.getKey().substring(0,entry.getKey().indexOf(':')),entry.getValue());
            } else {
                hashMap.put(entry.getKey(),entry.getValue());
            }
        }
        var generator1 = Paths.get(rootPath.getPath(),generatorName,"mappings",getFileFullName());
        try {
            System.out.println(generator1);
            Files.copy(new ByteArrayInputStream(hashMap.toString().replace("{","").
                    replace("}","").replace("=",": ").
                    replace(", ",System.lineSeparator()).getBytes(StandardCharsets.UTF_8)),generator1,(replace)?StandardCopyOption.REPLACE_EXISTING:StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ignored) {}
    }

    @Override
    public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
        return mCreatorPluginFactory.rootPath().equals(rootPath);
    }
}
