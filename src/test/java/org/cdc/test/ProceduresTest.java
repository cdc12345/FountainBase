package org.cdc.test;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.util.Locale;

public class ProceduresTest {

    @Test
    public void typeTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File("plugins"));
        mcr.createProcedure().setName("helloworld").markType().setColor(Color.RED).buildAndOutput();
    }

    @Test
    public void variableTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File("plugins"));
        mcr.createVariable().setColor(Color.RED).setName("hey").setNullable(true).setIgnoredByCoverage(true).buildAndOutput();
    }

    @Test
    public void versionTest() {
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File("plugins"));
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void procedureTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File("plugins"));
        mcr.createProcedure().setName("hey_set").setColor(Color.RED).setPreviousStatement(null)
                .setNextStatement(null).setGroup("name").setToolBoxId(BuiltInToolBoxId.Procedure.CUSTOM_VARIABLES)
                .setInputsInline(true).appendArgs0InputValue("hello", BuiltInTypes.Number.getLowerName())
                .appendArgs0InputValue("hello1",null).appendRequiredApi("helloworld").initGenerator().buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void aiTasksTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory("plugins");
        mcr.createAITask().setName("hey_am_you").setColor(Color.RED).setPreviousStatement(null).setNextStatement(null).setGroup("name").setInputsInline(true).setToolBoxId(BuiltInToolBoxId.AITasks.COMBAT_TASKS).appendArgs0FieldAIConditionSelector("ai").initGenerator().buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void triggersTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory("plugins");
        mcr.createTrigger().setName("hello").appendDependency("name","type").setCancelable(true).setSide(Side.Client).setHasResult(true).initGenerator().buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void datalistTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory("plugins");
        mcr.createDataList().setName("datalist").appendElement("hello").initGenerator().buildAndOutput();
        mcr.createDataList().setName("types").appendElement("hey").initGenerator().build();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void langTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory("plugins");
        mcr.createDefaultLanguage().appendLocalization("test","test").buildAndOutput();
        mcr.createLanguage(Locale.getDefault()).appendLocalization("test1","test1").buildAndOutput();

        Assertions.assertThrows(RuntimeException.class,()->{
            mcr.createProcedure().setName("test_lang").appendArgs0InputValue("hello","hey").setLanguage(mcr.createDefaultLanguage(),"hey");
        });
    }

    @Test
    public void apisTest(){
        MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory("plugins");
        mCreatorPluginFactory.createApis("helloworld");
    }
}
