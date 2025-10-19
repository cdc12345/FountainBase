package org.cdc.test;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.MCreatorVersions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MCreatorPluginFactoryTest {
    @Test
    public void testVersion202500(){
        MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory("plugins");
        mCreatorPluginFactory.setVersion(MCreatorVersions.Test.V_2025_0);
        var result = mCreatorPluginFactory.createDefaultLanguage().buildAndOutput();
        Assertions.assertNull(result);
		//
    }
}
