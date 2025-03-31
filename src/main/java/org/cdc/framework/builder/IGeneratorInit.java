package org.cdc.framework.builder;

import org.cdc.framework.MCreatorPluginFactory;

public interface IGeneratorInit {
    void initGenerator0(String generatorName);

    boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory);
}
