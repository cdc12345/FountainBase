package org.cdc.framework.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class DataListBuilder extends FileOutputBuilder<ArrayList<String>>{
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
    ArrayList<String> build() {
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
}
