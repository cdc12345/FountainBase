package org.cdc.framework.builder;


import org.cdc.framework.utils.FileUtils;

import java.io.File;
import java.io.IOException;

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
        return FileUtils.filterSpace(fileName) + "." +fileExtension;
    }

    public abstract T build();

    public abstract T buildAndOutput() throws IOException;
}
