package org.cdc.framework.builder;


import java.io.File;

public abstract class FileOutputBuilder<T> {
    protected final File rootPath;
    protected final File targetPath;

    protected String fileName;
    protected String fileExtension;

    protected FileOutputBuilder(File rootPath,File targetPath){
        this.rootPath = rootPath;
        this.targetPath = targetPath;
    }

    protected String getFileFullName(){
        return fileName + "." +fileExtension;
    }

    abstract T build();

    abstract T buildAndOutput();
}
