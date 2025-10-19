# Fountain

Fountain is a simple project to enjoy the autocomplete or datagen system in MCreator plugin development.

In this project, you can create a procedure quickly: 

```Java
	@Test public void procedureTest() {
	MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File(pluginPath));
	mcr.createProcedure().setName("hey_set").setCategory(BuiltInToolBoxId.Procedure.ADVANCED).setColor(Color.RED)
			.setPreviousStatement(null).setNextStatement(null)
			.appendArgs0InputValueWithDefaultToolboxInit("hello", BuiltInTypes.Number)
			.appendArgs0InputValue("placeholder", (String) null).appendArgs0StatementInput("statement")
			.statementBuilder().appendProvide("test", BuiltInTypes.Number).buildAndReturn()
			.appendRequiredApi("helloworld").initGenerator().buildAndOutput();

	mcr.createProcedure("advancements_clearall").setInputsInline(true).setColor("251").setPreviousStatement(null)
			.setNextStatement(null).setToolBoxId("unsafe").appendDependency("world", BuiltInTypes.World)
			.buildAndOutput();
	mcr.createProcedure("block_namespace").setInputsInline(true).setColor("%{BKY_TEXTS_HUE}")
			.setOutput(BuiltInTypes.String).setToolBoxId("blockdata").setGroup("name").buildAndOutput();
	mcr.initGenerator(Generators.FORGE1201);
}
```

you only need add these to your build.gradle:

```groovy
dependencies{
    //TODO: simple implements the FountainBase
}

tasks.jar {
    dependsOn("runDataGen")
}

tasks.processResources{
    exclude "lang/*.modifier"
}

tasks.register("runDataGen",JavaExec).configure {
    group = "build"
    main("org.cdc.framework.FountainBaseMain")
    classpath = sourceSets.main.runtimeClasspath
    workingDir(projectDir)

    doLast{
        copy {
            from sourceSets.main.resources
            into project.layout.buildDirectory.file("resources/main")
            exclude "lang/*.modifier"
        }
    }
}
```