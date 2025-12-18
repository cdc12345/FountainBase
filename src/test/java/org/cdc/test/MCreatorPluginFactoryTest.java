package org.cdc.test;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.MCreatorVersions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MCreatorPluginFactoryTest {

    private final String pluginPath = "build/plugins";

    @Test
    public void testVersion202500(){
        MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(pluginPath);
        mCreatorPluginFactory.setVersion(MCreatorVersions.Test.V_2025_0);
        var result = mCreatorPluginFactory.createDefaultLanguage().buildAndOutput();
        Assertions.assertNull(result);
    }

    @Test
    public void testElementIcon() throws IOException {
        MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(pluginPath);
        mCreatorPluginFactory.getToolKit().addElementIcon("test",new ByteArrayInputStream("".getBytes()));
    }
}
