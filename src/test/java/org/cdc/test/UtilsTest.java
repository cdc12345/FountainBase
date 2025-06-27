package org.cdc.test;

import org.cdc.framework.utils.ColorUtils;
import org.cdc.framework.utils.FileUtils;
import org.cdc.framework.utils.MCreatorVersions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class UtilsTest {
    @Test
    public void testHueColor(){
        Assertions.assertEquals(ColorUtils.colorHue("helloworld"),"%{BKY_helloworld_HUE}");
    }

    @Test
    public void testGenerateCode(){
        System.out.println(FileUtils.tryGenerateProcedureBuilderCode(new File("src/test/resources/advancements_clearall.json")));
        System.out.println(FileUtils.tryGenerateProcedureBuilderCode(new File("src/test/resources/block_namespace.json")));
        System.out.println(FileUtils.tryGenerateVariableCode(new File("src/test/resources/atomicitemstack.json")));
        System.out.println(FileUtils.tryGenerateTrigger(new File("src/test/resources/effect_applicable.json")));
    }

    @Test
    public void testFileName(){
        Assertions.assertEquals(FileUtils.filterSpace("hello world"),"hello_world");
    }

    @Test
    public void testVersions(){
        Assertions.assertEquals("2025001", MCreatorVersions.toFormattedVersion(MCreatorVersions.V_2025_1));
        Assertions.assertEquals("202500199999",MCreatorVersions.toFormattedVersion(MCreatorVersions.V_2025_1_9999));
    }
}
