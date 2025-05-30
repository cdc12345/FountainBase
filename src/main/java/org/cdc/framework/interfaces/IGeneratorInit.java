package org.cdc.framework.interfaces;

import org.cdc.framework.MCreatorPluginFactory;

public interface IGeneratorInit {

    void initGenerator0(String generatorName,boolean replace);

    default void initGenerator0(String generatorName){
        initGenerator0(generatorName,false);
    }

    boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory);
}
