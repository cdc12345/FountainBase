package org.cdc.test;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;
import org.cdc.framework.utils.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ProceduresTest {

    private final String pluginPath = "build/plugins";

    @Test
    public void typeTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
        mcr.createProcedureCategory("helloworld").markType().setColor(Color.RED).buildAndOutput();
        mcr.createProcedure().markType().setName("testtype1").setParentCategory("helloworld").setColor(Color.BLUE).buildAndOutput();

        mcr.createAITaskCategory("helloai").setColor(Color.RED).buildAndOutput();
    }

    @Test
    public void variableTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
        mcr.createVariable().setName("hey").setColor(Color.RED).setNullable(true).setIgnoredByCoverage(true).buildAndOutput();
        mcr.createVariable().setName("atomicitemstack").setColor(255).setBlocklyVariableType("AtomicItemStack").setNullable(false).setIgnoredByCoverage(true).buildAndOutput();
    }

    @Test
    public void generatorTest() {
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
        mcr.initGenerator(Generators.FORGE1201);
        Assertions.assertTrue(Files.exists(Path.of(mcr.rootPath().getPath(), "forge-1.20.1")));
    }

    @Test
    public void procedureTest(){
        MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
        mcr.createProcedure().setName("hey_set").setColor(Color.RED).setPreviousStatement(null)
                .setNextStatement(null)
                .appendArgs0InputValue("hello", BuiltInTypes.Number.getLowerName())
                .appendArgs0InputValue("hello1",(String) null).appendRequiredApi("helloworld").toolBoxInitBuilder().setName("hello").appendConstantNumber(1).buildAndReturn().initGenerator().buildAndOutput();
        Assertions.assertThrows(RuntimeException.class, ()-> mcr.createProcedure().buildAndOutput());

        mcr.createProcedure("advancements_clearall").setInputsInline(true).setColor("251").setPreviousStatement(null).setNextStatement(null).setToolBoxId("unsafe").appendDependency("world",BuiltInTypes.World).buildAndOutput();
        mcr.createProcedure("block_namespace").setInputsInline(true).setColor("%{BKY_TEXTS_HUE}").setOutput(BuiltInTypes.String).setToolBoxId("blockdata").setGroup("name").buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void aiTasksTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
        mcr.createAITask("hey_am_you").setColor(Color.RED).setPreviousStatement(null).setNextStatement(null).setGroup("name").setInputsInline(true).setToolBoxId(BuiltInToolBoxId.AITasks.COMBAT_TASKS).appendArgs0FieldAIConditionSelector("ai").initGenerator().buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void triggersTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
        mcr.createTrigger().setName("hello").appendDependency("name","type").setCancelable(true).setSide(Side.Client).setHasResult(true).initGenerator().buildAndOutput();
        mcr.createTrigger().setName("effect_applicable").setCancelable(true).setHasResult(true).appendRequiredApi("hey").appendDependency("entity",BuiltInTypes.Entity).appendDependency("world",BuiltInTypes.World).appendDependency("x",BuiltInTypes.Number).appendDependency("y",BuiltInTypes.Number).appendDependency("z",BuiltInTypes.Number).appendDependency("effect","object").buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void datalistTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
        mcr.createDataList().setName("datalist").appendElement("hello").initGenerator().buildAndOutput();
        mcr.createDataList().setName("types").appendElement("hey","hello").initGenerator().build();
        mcr.createDataList().setName("blocksitems").appendElement("""
                external: 
                   readable_name: "Air"
                   type: block""", """
                  
                  - Blocks.AI
                  - "air"
                """).initGenerator().buildAndOutput();
        mcr.initGenerator(Generators.FORGE1201);
    }

    @Test
    public void langTest(){
        MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
        mcr.createDefaultLanguage().appendLocalization("test","test").buildAndOutput();
        mcr.createLanguage(Locale.getDefault()).appendLocalization("test1","test1").buildAndOutput();

        Assertions.assertThrows(RuntimeException.class,()-> mcr.createProcedure().setName("test_lang").appendArgs0InputValue("hello","hey").setLanguage(mcr.createDefaultLanguage(),"hey"));
    }

    @Test
    public void apisTest(){
        MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(pluginPath);
        mCreatorPluginFactory.createApis("helloworld");
    }
}
