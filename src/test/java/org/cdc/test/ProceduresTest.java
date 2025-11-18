package org.cdc.test;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;
import org.cdc.framework.utils.parser.DefaultParameterConvertor;
import org.cdc.framework.utils.parser.MethodParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static org.cdc.framework.utils.yaml.YamlDataUtils.str;

public class ProceduresTest {

	private final String pluginPath = "build/plugins";

	@Test public void typeTest() throws IOException {
		System.out.println(new File(pluginPath).getAbsolutePath());
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
		mcr.createProcedureCategory("helloworld").markType().setColor(Color.RED).buildAndOutput();
		mcr.createProcedure().markType().setName("testtype1").setParentCategory("helloworld").setColor(Color.BLUE)
				.buildAndOutput();

		mcr.createAITaskCategory("helloai").setColor(Color.RED).buildAndOutput();
	}

	@Test public void variableTest() throws IOException {
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
		mcr.createVariable().setName("hey").setColor(Color.RED).setNullable(true).setIgnoredByCoverage(true)
				.buildAndOutput();
		mcr.createVariable().setName("atomicitemstack").setColor(255).setBlocklyVariableType("AtomicItemStack")
				.setNullable(false).setIgnoredByCoverage(true).buildAndOutput();
	}

	@Test public void generatorTest() throws IOException {
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
		mcr.createVariable().setName("testVariable").setColor(Color.RED).setDefaultValue("empty").initGenerator()
				.buildAndOutput();
		mcr.initGenerator(Generators.FORGE1201);
		Assertions.assertTrue(Files.exists(Path.of(mcr.rootPath().getPath(), "forge-1.20.1")));

		mcr.createInfo().setId("test").setAuthor("cdc12345").addSupportedVersion(MCreatorVersions.V_2025_1)
				.setName("test");
	}

	@Test public void procedureTest() throws IOException {
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
		mcr.createProcedure().setName("hey_set").setCategory(BuiltInToolBoxId.Procedure.ADVANCED).setColor(Color.RED)
				.setPreviousStatement(null).setNextStatement(null)
				.appendArgs0InputValueWithDefaultToolboxInit("hello", BuiltInTypes.Entity)
				.appendArgs0InputValue("placeholder", (String) null)
				.appendArgs0InputValue("iterator", BuiltInTypes.Entity).toolBoxInitBuilder().setName("iterator")
				.appendEntityIterator().buildAndReturn().appendArgs0StatementInput("statement").statementBuilder()
				.setName("statement").appendProvide("test", BuiltInTypes.Number).buildAndReturn()
				.appendRequiredApi("helloworld").setGeneratorListener(a -> {
					MethodParser methodParser = new MethodParser();
					methodParser.setParameterStringFunction(new DefaultParameterConvertor());
					try {
						methodParser.parseClass(Objects.requireNonNull(this.getClass().getResource("ParsedClass.java")).openStream());
						methodParser.parseMethod("hey_set");
						Files.copy(new ByteArrayInputStream(methodParser.toFTLContent().getBytes()), a,
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					return false;
				}).initGenerator().buildAndOutput();
		Assertions.assertThrows(RuntimeException.class, () -> mcr.createProcedure().buildAndOutput());

		mcr.createProcedure("advancements_clearall").setInputsInline(true).setColor("251").setPreviousStatement(null)
				.setNextStatement(null).setToolBoxId("unsafe").appendDependency("world", BuiltInTypes.World)
				.buildAndOutput();
		mcr.createProcedure("block_namespace").setInputsInline(true).setColor("%{BKY_TEXTS_HUE}")
				.setOutput(BuiltInTypes.String).setToolBoxId("blockdata").setGroup("name").buildAndOutput();
		mcr.initGenerator(Generators.FORGE1201);
	}

	@Test public void aiTasksTest() throws IOException {
		MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
		mcr.createAITask("hey_am_you").setColor(Color.RED).setPreviousStatement(null).setNextStatement(null)
				.setGroup("name").setInputsInline(true).setToolBoxId(BuiltInToolBoxId.AITasks.COMBAT_TASKS)
				.appendArgs0FieldAIConditionSelector("ai").initGenerator().buildAndOutput();
		mcr.initGenerator(Generators.FORGE1201);
	}

	@Test public void triggersTest() throws IOException {
		MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
		mcr.createTrigger().setName("hello").appendDependency("name", "type").setCancelable(true).setSide(Side.Client)
				.setHasResult(true).initGenerator().buildAndOutput();
		mcr.createTrigger().setName("effect_applicable").setCancelable(true).setHasResult(true).appendRequiredApi("hey")
				.appendDependency("entity", BuiltInTypes.Entity).appendDependency("world", BuiltInTypes.World)
				.appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number)
				.appendDependency("z", BuiltInTypes.Number).appendDependency("effect", "object").buildAndOutput();
		mcr.initGenerator(Generators.FORGE1201);
	}

	@Test public void datalistTest() throws IOException {
		MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
		mcr.createDataList().setName("datalist").appendElement("hello").initGenerator().buildAndOutput();
		mcr.createDataList().setName("types").appendElement("hey", "hello").initGenerator().build();
		mcr.createDataList().setName("blocksitems").appendElement("""
				external: 
				   readable_name: "Air"
				   type: block""", """
				
				  - Blocks.AI
				  - "air"
				""").initGenerator().buildAndOutput();
		mcr.createDataList("testexternal").appendElement("test1", "test", List.of("1"))
				.appendElement("test2", Map.of("read", "book"), List.of(str("test"))).initGenerator().buildAndOutput();
		mcr.initGenerator(Generators.FORGE1201,true);
	}

	@Test public void langTest() {
		MCreatorPluginFactory mcr = MCreatorPluginFactory.createFactory(pluginPath);
		mcr.createDefaultLanguage().appendLocalization("test", "test").buildAndOutput();
		mcr.createLanguage(Locale.getDefault()).appendLocalization("test1", "test1").buildAndOutput();

		Assertions.assertThrows(RuntimeException.class,
				() -> mcr.createProcedure().setName("test_lang").appendArgs0InputValue("hello", "hey")
						.setLanguage(mcr.createDefaultLanguage(), "hey"));
	}

	@Test public void apisTest() {
		MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(pluginPath);
		mCreatorPluginFactory.createApis("helloworld");
	}
}
