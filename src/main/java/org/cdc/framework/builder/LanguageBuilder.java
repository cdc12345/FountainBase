package org.cdc.framework.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class LanguageBuilder extends FileOutputBuilder<Properties> {

    private final Properties result;

    public LanguageBuilder(File rootPath,String fileName) {
        super(rootPath, new File(rootPath,"lang"));
        this.result = new Properties();

        this.fileName = fileName;
        this.fileExtension = "properties";

        load();
    }
    @CanIgnoreReturnValue
    public LanguageBuilder load(){
        try {
            this.result.load(new FileReader(new File(targetPath,fileName+"."+fileExtension)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public LanguageBuilder clear(){
        result.clear();
        return this;
    }

    public LanguageBuilder appendLocalization(String key,String value){
        this.result.setProperty(key,value);
        return this;
    }
    @CanIgnoreReturnValue
    public LanguageBuilder appendProcedure(String proName,String value){
        return appendLocalization("blockly.block."+proName,value);
    }
    @CanIgnoreReturnValue
    public LanguageBuilder appendTrigger(String triggerName,String value){
        return appendLocalization("trigger."+triggerName,value);
    }

    public LanguageBuilder appendProcedureToolTip(String proName,String value){
        return appendLocalization("blockly.block."+proName+".tooltip",value);
    }

    @CanIgnoreReturnValue
    public LanguageBuilder appendProcedureCategory(String category,String value){
        return appendLocalization("blockly.category."+category,value);
    }

    @Override
    Properties build() {
        return null;
    }

    @Override
    public Properties buildAndOutput() {
        try {
            this.result.store(new FileWriter(new File(targetPath,fileName+"."+fileExtension)),"Auto-Generated");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.result;
    }
}
