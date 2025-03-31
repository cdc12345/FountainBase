package org.cdc.framework.builder.v_2025_0;

import java.io.File;
import java.util.Properties;

public class LanguageBuilder extends org.cdc.framework.builder.LanguageBuilder {
    public LanguageBuilder(File rootPath, String fileName) {
        super(rootPath, fileName);
    }

    @Override
    public Properties buildAndOutput() {
        System.out.println("This is a joke");
        super.buildAndOutput();
        return null;
    }
}
