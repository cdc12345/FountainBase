package org.cdc.test;

import org.cdc.framework.MCreatorPlugin;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

public class ProceduresTest {

    @Test
    public void typeTest(){
        MCreatorPlugin mcr = new MCreatorPlugin(new File("plugins"));
        mcr.createProcedure().setName("helloworld").markType().setColor(Color.RED).buildAndOutput();
    }

    @Test
    public void variableTest(){
        MCreatorPlugin mcr = new MCreatorPlugin(new File("plugins"));
        mcr.createVariable().setColor(Color.RED).setName("hey").setNullable(true).setIgnoredByCoverage(true).buildAndOutput();
    }

    @Test
    public void versionTest(){
        MCreatorPlugin mcr = new MCreatorPlugin(new File("plugins"));
        mcr.initGenerator("forge-1.20.1");
    }
}
