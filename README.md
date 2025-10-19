# Fountain

Fountain is a simple project to enjoy the autocomplete or datagen system in MCreator plugin development.

In this project, you can create a procedure quickly: 

```Java
mcr.createProcedure().setName("hey_set").setColor(Color.RED).setPreviousStatement(null)
                .setNextStatement(null)
                .appendArgs0InputValueWithDefaultToolboxInit("hello", BuiltInTypes.Number)
                .appendArgs0InputValue("placeholder",(String) null).appendRequiredApi("helloworld").initGenerator().buildAndOutput();
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