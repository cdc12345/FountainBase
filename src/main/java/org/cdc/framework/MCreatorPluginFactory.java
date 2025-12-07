package org.cdc.framework;

import org.cdc.framework.builder.*;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.interfaces.IVariableType;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.FileUtils;
import org.cdc.framework.utils.MCreatorVersions;
import org.jetbrains.annotations.Contract;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

public class MCreatorPluginFactory {

	//generator need to be inited
	private final ArrayList<IGeneratorInit> generatorInits = new ArrayList<>();

	@Contract("null->null")
	public static MCreatorPluginFactory createFactory(String folder) {
		if (folder == null){
			return null;
		}
		return new MCreatorPluginFactory(new File(folder));
	}

	private static final String CURRENT_VERSION = MCreatorVersions.V_2025_3;

	private String currentInit;

	private final File rootPath;
	private String version;

	private final ToolKit toolkit;

	public MCreatorPluginFactory(File rootPath) {
		this.rootPath = rootPath;
		this.version = MCreatorVersions.V_2025_3;
		this.toolkit = new ToolKit();
	}

	public void createFolder(String name) {
		var file = new File(rootPath, name);
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}
	}

	public void createProcedureTemplateFolder() {
		createFolder("templates/ptpl");
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void initGenerator(String generator) {
		initGenerator(generator, false);
	}

	public void initGenerator(String generator, boolean replace) {
		getToolKit().lastGenerator = generator;
		createFolder(generator);
		var generator1 = new File(rootPath, generator);
		var file = new File(generator1, "aitasks");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		file = new File(generator1, "mappings");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		file = new File(generator1, "procedures");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		file = new File(generator1, "triggers");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		file = new File(generator1, "variables");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		file = new File(generator1, "templates");
		if (file.mkdirs()) {
			System.out.println(file.getPath());
		}

		currentInit = generator;
		generatorInits.forEach(a -> {
			if (a.isSupported(this)) {
				a.initGenerator0(generator, replace);
			}
		});
		currentInit = null;
	}

	public PluginInfoBuilder createInfo() {
		return new PluginInfoBuilder(rootPath);
	}

	public ProcedureBuilder createProcedure() {
		return createProcedure(null);
	}

	public ProcedureBuilder createProcedure(String name) {
		createFolder("procedures");
		ProcedureBuilder builder = new ProcedureBuilder(rootPath);
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".ProcedureBuilder");
				builder = (ProcedureBuilder) class1.getConstructor(new Class[] { File.class }).newInstance(rootPath);
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		if (name != null) {
			builder.setName(name);
		}
		generatorInits.add(builder);
		return builder;
	}

	public AITasksBuilder createAITask() {
		return createAITask(null);
	}

	public AITasksBuilder createAITask(String name) {
		createFolder("aitasks");
		AITasksBuilder builder = new AITasksBuilder(rootPath);
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".AITasksBuilder");
				builder = (AITasksBuilder) class1.getConstructor(new Class[] { File.class }).newInstance(rootPath);
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		if (name != null) {
			builder.setName(name);
		}
		generatorInits.add(builder);
		return builder;
	}

	public VariableBuilder createVariable() {
		return createVariable(null);
	}

	public VariableBuilder createVariable(IVariableType type) {
		createFolder("variables");
		VariableBuilder builder = new VariableBuilder(rootPath);
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".VariableBuilder");
				builder = (VariableBuilder) class1.getConstructor(new Class[] { File.class }).newInstance(rootPath);
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		if (type != null) {
			builder.setName(type.getVariableType()).setBlocklyVariableType(type.getBlocklyVariableType());
		}
		generatorInits.add(builder);
		return builder;
	}

	public LanguageBuilder createDefaultLanguage() {
		createFolder("lang");
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".LanguageBuilder");
				return (LanguageBuilder) class1.getConstructor(new Class[] { File.class, String.class })
						.newInstance(rootPath, "texts");
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return new LanguageBuilder(rootPath, "texts");
	}

	@Contract("null->fail")
	public LanguageBuilder createLanguage(Locale locale) {
		createFolder("lang");
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".LanguageBuilder");
				return (LanguageBuilder) class1.getConstructor(new Class[] { File.class, String.class })
						.newInstance(rootPath, "texts_" + locale.getLanguage() + "_" + locale.getCountry());
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return new LanguageBuilder(rootPath, "texts_" + locale.getLanguage() + "_" + locale.getCountry());
	}

	public DataListBuilder createDataList() {
		return createDataList(null);
	}

	public DataListBuilder createDataList(String name) {
		createFolder("datalists");
		DataListBuilder builder = new DataListBuilder(rootPath);
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".DataListBuilder");
				builder = (DataListBuilder) class1.getConstructor(new Class[] { File.class }).newInstance(rootPath);
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		generatorInits.add(builder);
		return builder.setName(name);
	}

	public TriggerBuilder createTrigger() {
		return createTrigger(null);
	}

	public TriggerBuilder createTrigger(String name) {
		createFolder("triggers");
		TriggerBuilder builder = new TriggerBuilder(rootPath);
		if (!CURRENT_VERSION.equals(version)) {
			try {
				var class1 = this.getClass().getClassLoader()
						.loadClass("org.cdc.framework.builder." + version + ".TriggerBuilder");
				builder = (TriggerBuilder) class1.getConstructor(new Class[] { File.class }).newInstance(rootPath);
			} catch (ClassNotFoundException ignored) {

			} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		generatorInits.add(builder);
		return builder.setName(name);
	}

	public void createApis(String apiName) {
		createFolder("apis");
		try {
			Files.copy(new ByteArrayInputStream(("name: " + apiName).getBytes(StandardCharsets.UTF_8)),
					Path.of(rootPath.getPath(), "apis", apiName + ".yaml"));
		} catch (IOException ignored) {
		}
	}

	public ProcedureBuilder createProcedureCategory(String name) {
		return BuilderUtils.createProcedureCategory(this, name);
	}

	public ProcedureBuilder createAITaskCategory(String name) {
		return BuilderUtils.createAITaskCategory(this, name);
	}

	public File rootPath() {
		return rootPath;
	}

	public String getCurrentInit() {
		return currentInit;
	}

	public ToolKit getToolKit() {
		return toolkit;
	}

	public class ToolKit {

		private String lastGenerator;

		private ToolKit() {
		}

		public ProcedureBuilder createInputProcedure(String name) {
			return BuilderUtils.createCommonProcedure(MCreatorPluginFactory.this, name);
		}

		public ProcedureBuilder createEndProcedure(String name) {
			return BuilderUtils.createEndProcedure(MCreatorPluginFactory.this, name);
		}

		public ProcedureBuilder createOutputProcedure(String name, String output) {
			return BuilderUtils.createOutputProcedure(MCreatorPluginFactory.this, name, output);
		}

		public ProcedureBuilder createOutputProcedure(String name, IVariableType output) {
			return BuilderUtils.createOutputProcedure(MCreatorPluginFactory.this, name, output);
		}

		public ProcedureBuilder createProcedureWithStatement(String name, String statementName,
				IVariableType placeholdertype, String statementProviderName, IVariableType statementProviderType) {
			return BuilderUtils.createProcedureWithStatement(MCreatorPluginFactory.this, name, statementName,
					placeholdertype, statementProviderName, statementProviderType);
		}

		public ProcedureBuilder createProcedureWithEntityIterator(String name, String statementName) {
			return BuilderUtils.createProcedureWithStatement(MCreatorPluginFactory.this, name, statementName,
					BuiltInTypes.Entity, "entityiterator", BuiltInTypes.Entity);
		}

		public String getCurrentInitGenerator() {
			return MCreatorPluginFactory.this.currentInit;
		}

		public void clearGenerator(String generatorName) {
			FileUtils.deleteEmptyDirectoryInDirectory(new File(rootPath, generatorName));
		}

		public void clearGenerator() {
			if (lastGenerator != null) {
				clearGenerator(lastGenerator);
			}
		}

		public void clearPlugin() {
			FileUtils.deleteEmptyDirectoryInDirectory(rootPath);
		}
	}
}
