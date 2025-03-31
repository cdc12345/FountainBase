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


        loadDefault();
        load();
    }

    @CanIgnoreReturnValue
    public LanguageBuilder loadDefault(){
        if (!"texts".equals(fileName))
            return this;
        try {
            var file = new File(targetPath,"texts."+fileExtension);
            if (file.exists())
                this.result.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @CanIgnoreReturnValue
    public LanguageBuilder load(){
        try {
            var file = new File(targetPath,fileName+"."+fileExtension);
            if (file.exists())
                this.result.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public LanguageBuilder clear(){
        result.clear();
        return this;
    }

    /**
     * blockly.block.atomic_itemstack_set=Set itemstack atomic %1 to %2
     * @param key key
     * @param value value
     * @return this
     */
    public LanguageBuilder appendLocalization(String key,String value){
        this.result.setProperty(key,value);
        return this;
    }

    /**
     * blockly.block.atomic_itemstack_set=Set itemstack atomic %1 to %2
     * @param proName procedure's name
     * @param value value
     * @return this
     */
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
    public Properties build() {
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
