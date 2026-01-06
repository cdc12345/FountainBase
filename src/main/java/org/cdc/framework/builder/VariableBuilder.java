package org.cdc.framework.builder;

import com.google.errorprone.annotations.DoNotCall;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.ColorUtils;
import org.cdc.framework.utils.FileUtils;

import javax.management.DescriptorKey;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VariableBuilder extends JsonBuilder implements IGeneratorInit {

	private final JsonArray requiredApis;

	private String defaultValue;

	private final Flags flags = new Flags();

	private static class Flags {
		protected boolean flagToInitGenerator;
	}

	public VariableBuilder(File rootPath) {
		super(rootPath, new File(rootPath, "variables"));
		this.result = new JsonObject();

		this.requiredApis = new JsonArray();
	}

	public VariableBuilder setName(String name) {
		this.fileName = FileUtils.filterSpace(name);
		return this;
	}

	public VariableBuilder setColor(int color) {
		result.getAsJsonObject().add("color", new JsonPrimitive(color));
		return this;
	}

	public VariableBuilder setColor(String color) {
		result.getAsJsonObject().add("color", new JsonPrimitive(color));
		return this;
	}

	public VariableBuilder setColor(Color color) {
		return setColor(ColorUtils.toHex(color));
	}

	public VariableBuilder setBlocklyVariableType(String type) {
		result.getAsJsonObject().addProperty("blocklyVariableType", type);
		return this;
	}

	public VariableBuilder setNullable(boolean nullable) {
		result.getAsJsonObject().addProperty("nullable", nullable);
		return this;
	}

	/**
	 * coverage
	 * @param ignoredByCoverage boolean
	 * @return this
	 */
	public VariableBuilder setIgnoredByCoverage(boolean ignoredByCoverage) {
		result.getAsJsonObject().addProperty("ignoredByCoverage", ignoredByCoverage);
		return this;
	}

	/**
	 * this will be generated after initGenerator
	 * @param defaultValue default value
	 * @return this
	 */
	public VariableBuilder setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	//Localizations

	/**
	 * blockly.block.get_var__{fileName} = {text}
	 * @param languageBuilder language
	 * @param text eg. get ActionResultType
	 * @return this
	 */
	public VariableBuilder setGetterText(LanguageBuilder languageBuilder, String text) {
		languageBuilder.appendLocalization("blockly.block.get_var_" + this.fileName, text);
		return this;
	}

	/**
	 * blockly.block.set_var_{fileName} = {text}
	 * @param languageBuilder language
	 * @param text eg. set ActionResultType
	 * @return this
	 */
	public VariableBuilder setSetterText(LanguageBuilder languageBuilder, String text) {
		languageBuilder.appendLocalization("blockly.block.set_var_" + this.fileName, text);
		return this;
	}

	/**
	 * blockly.block.return_{fileName} = {text}
	 * @param languageBuilder language
	 * @param text eg. return ActionResultType
	 * @return this
	 */
	public VariableBuilder setReturnText(LanguageBuilder languageBuilder, String text) {
		languageBuilder.appendLocalization("blockly.block.return_" + this.fileName, text);
		return this;
	}

	/**
	 * blockly.block.custom_dependency_{fileName} = {text}
	 * @param languageBuilder language
	 * @param text eg. ActionResultType Dependency
	 * @return this
	 */
	public VariableBuilder setCustomDependency(LanguageBuilder languageBuilder, String text) {
		languageBuilder.appendLocalization("blockly.block.custom_dependency_" + this.fileName, text);
		return this;
	}

	public VariableBuilder setCallProcedureRetval(LanguageBuilder languageBuilder, String text) {
		languageBuilder.appendLocalization("blockly.block.procedure_retval_" + this.fileName, text);
		return this;
	}

	/**
	 * this is not implement in 2025.1
	 *
	 * @param name name
	 * @return this
	 */
	@DoNotCall("this version not supported") public VariableBuilder appendRequiredApi(String name) {
		requiredApis.add(name);
		return this;
	}

	@Override public JsonElement build() {
		JsonObject resul = result.getAsJsonObject();
		if (!resul.has("nullable")) {
			setNullable(false);
		}
		if (!requiredApis.isEmpty()) {
			this.result.getAsJsonObject().add("required_apis", requiredApis);
		}
		return result;
	}

	@DescriptorKey("Must edit variable.yaml") public VariableBuilder initGenerator() {
		flags.flagToInitGenerator = true;
		return this;
	}

	@Override public void initGenerator0(String generatorName, boolean replace) {
		var generator1 = Paths.get(rootPath.getPath(), generatorName, targetPath.getName(), fileName + ".yaml");
		try {
			if (!Files.exists(generator1)) {
				System.out.println("Before output, please edit the file. Or you will crash your mcreator!");
				StringBuilder builder = new StringBuilder("#AutoGenerated");
				if (defaultValue != null) {
					String loaderName = generatorName.split("-")[0];
					try {
						InputStream inputStream = getClass().getResourceAsStream(
								"/templates/variables/" + loaderName + ".yaml");
						if (inputStream == null) {
							inputStream = Thread.currentThread().getContextClassLoader()
									.getResourceAsStream("templates/variables/default.yaml");
						}
						if (inputStream != null) {
							builder.append(System.lineSeparator());
							var out = new String(inputStream.readAllBytes());
							builder.append(String.format(out,defaultValue));
							inputStream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Files.copy(new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8)),
						generator1);
				System.out.println(generator1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
		return flags.flagToInitGenerator && mCreatorPluginFactory.rootPath().equals(rootPath)
				&& BuilderUtils.isSupportProcedure(mCreatorPluginFactory.getCurrentInit());
	}
}
