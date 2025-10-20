package org.cdc.framework.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.interfaces.IVariableType;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.FileUtils;
import org.cdc.framework.utils.Side;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TriggerBuilder extends JsonBuilder implements IGeneratorInit {

	protected final JsonArray dependencies;
	protected final JsonArray requiredApis;
	protected final Flags flags;

	protected static class Flags {
		protected boolean flagToSetLang;
		protected boolean flagToInitGenerator;
	}

	public TriggerBuilder(File rootPath) {
		super(rootPath, new File(rootPath, "triggers"));

		this.flags = new Flags();
		this.result = new JsonObject();
		this.dependencies = new JsonArray();
		this.requiredApis = new JsonArray();
		this.result.getAsJsonObject().add("dependencies_provided", dependencies);

		this.result.getAsJsonObject().add("required_api", requiredApis);
	}

	public TriggerBuilder setName(String name) {
		this.fileName = FileUtils.filterSpace(name);
		return this;
	}

	public TriggerBuilder setHasResult(boolean hasResult) {
		this.result.getAsJsonObject().addProperty("has_result", hasResult);
		return this;
	}

	public TriggerBuilder setCancelable(boolean cancelable) {
		this.result.getAsJsonObject().addProperty("cancelable", cancelable);
		return this;
	}

	public TriggerBuilder setSide(Side side) {
		this.result.getAsJsonObject().addProperty("side", side.name().toLowerCase());
		return this;
	}

	/**
	 * {
	 * "name": "z",
	 * "type": "number"
	 * }
	 *
	 * @param name 依赖名称
	 * @param type 依赖类型.类似于number
	 * @return this
	 */
	public TriggerBuilder appendDependency(String name, String type) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("type", type);
		dependencies.add(jsonObject);
		return this;
	}

	public TriggerBuilder appendDependency(String name, IVariableType type) {
		appendDependency(name, type.getVariableType());
		return this;
	}

	public TriggerBuilder appendRequiredApi(String name) {
		requiredApis.add(name);
		return this;
	}

	public TriggerBuilder setLocalizedName(LanguageBuilder languageBuilder, String name) {
		return setLanguage(languageBuilder, name);
	}

	public TriggerBuilder setLanguage(LanguageBuilder languageBuilder, String content) {
		flags.flagToSetLang = true;
		languageBuilder.appendTrigger(fileName, content);
		return this;
	}

	@Override public JsonElement build() {
		if (requiredApis.isEmpty()) {
			result.getAsJsonObject().remove("required_api");
		}
		return result;
	}

	public TriggerBuilder initGenerator() {
		flags.flagToInitGenerator = true;
		return this;
	}

	@Override public void initGenerator0(String generatorName, boolean replace) {
		var generator1 = Paths.get(rootPath.getPath(), generatorName, "triggers", fileName + ".java.ftl");
		System.out.println(generator1);
		StringBuilder builder = new StringBuilder();
		builder.append(dependencies.asList().stream().filter(JsonElement::isJsonObject)
				.map(a -> "${parameter$" + a.getAsJsonObject().get("name").getAsString() + "}")
				.collect(Collectors.joining(",", "<#-- ", " -->")));
		builder.append(System.lineSeparator());
		String loaderName = generatorName.split("-")[0];
		String generatedDependencies = BuilderUtils.generateTriggerDependencies(new HashMap() {
			{
				dependencies.asList().forEach(a -> {
					put(a.getAsJsonObject().get("name").getAsString(), "");
				});
			}
		});
		try {
			InputStream inputStream = getClass().getResourceAsStream("/templates/triggers/" + loaderName + ".txt");
			if (inputStream == null) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("templates/triggers/default.txt");
			}
			builder.append(String.format(new String(inputStream.readAllBytes()), generatedDependencies));
			inputStream.close();
		} catch (Exception e) {
			builder.append("<#-- ").append(e).append(" -->");
			builder.append(System.lineSeparator());
			builder.append(generatedDependencies);
		}
		try {
			Files.copy(new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8)), generator1);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	@Override public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
		return flags.flagToInitGenerator && mCreatorPluginFactory.rootPath().equals(rootPath)
				&& BuilderUtils.isSupportProcedure(mCreatorPluginFactory.getCurrentInit());
	}

	@Override public JsonElement buildAndOutput() {
		if (!flags.flagToSetLang) {
			System.err.println("You should know the lang of " + fileName + " is not generated");
		}
		return super.buildAndOutput();
	}
}
