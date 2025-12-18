package org.cdc.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class FileUtils {
	@Contract("null->_")
	public static void deleteNonEmptyDirector(File directory) {
		if (directory == null) {
			return;
		}
		try {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (!file.delete()) {
						deleteNonEmptyDirector(file);
					}
				}
			}
			directory.delete();

		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public static void deleteEmptyDirectoryInDirectory(File directory) {
		if (directory == null) {
			return;
		}
		try {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isDirectory())
						file.delete();
				}
			}

		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	@Contract("null->fail")
	public static String loadStringFromFile(Path file) {
		try {
			return Files.readString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Contract("null->fail")
	public static JsonArray loadInputsFromFile(Path file) {
		Gson gson = new Gson();
		var js = gson.fromJson(loadStringFromFile(file), JsonObject.class);
		return gson.fromJson(js.get("mcreator").getAsJsonObject().get("inputs"), JsonArray.class);
	}

	@Contract("null->null")
	public static String filterSpace(String name) {
		if (name == null) {
			return null;
		}
		return name.replace(" ", "_");
	}

	/**
	 * generate code
	 *
	 * @param fl file
	 * @return generatedCode
	 */
	public static String tryGenerateProcedureBuilderCode(Path fl) {
		return tryGenerateProcedureBuilderCode(loadStringFromFile(fl), getFileNameWithExtension(fl), "pluginFactory");
	}

	public static String tryGenerateProcedureBuilderCode(String json, String fileName,
			String pluginFactoryVariableName) {
		Function<String, String> buildTypesConvertor = check -> {
			BuiltInTypes ch;
			if ((ch = BuiltInTypes.getType(check)) != null) {
				return "BuiltInTypes." + ch.name();
			} else {
				return "\"" + check + "\"";
			}
		};
		Gson gson = new Gson();
		var file = gson.fromJson(json, JsonObject.class);
		StringBuilder builder = new StringBuilder();
		builder.append(pluginFactoryVariableName).append(".createProcedure(\"").append(fileName).append("\")");
		if (fileName.startsWith("$")) {
			builder.append(".markType()");
		}
		if (file.has("args0")) {
			JsonArray args0 = file.getAsJsonArray("args0");
			for (JsonElement jsonElement : args0) {
				var arg = jsonElement.getAsJsonObject();
				var type = arg.get("type").getAsString();

				switch (type) {
				case "input_value" -> {
					var name = arg.get("name").getAsString();
					builder.append(".appendArgs0InputValue(\"").append(name).append("\"");
					if (arg.has("check")) {
						var check = arg.get("check").getAsString();
						builder.append(", ").append(buildTypesConvertor.apply(check)).append(")");
					} else {
						builder.append(")");
					}
				}
				case "input_statement" -> {
					var name = arg.get("name").getAsString();
					builder.append(".appendArgs0StatementInput(\"").append(name).append("\")");
				}
				case "field_image" -> {
					var src = arg.get("src").getAsString();
					var width = arg.get("width").getAsInt();
					var height = arg.get("height").getAsInt();
					builder.append(".appendArgs0FieldImage(\"").append(src).append("\", ").append(width).append(", ")
							.append(height).append(")");
				}
				default -> {
					builder.append("/*Missing %s*/".formatted(arg.toString()));
				}
				}
			}
		}
		if (file.has("extensions")) {
			JsonArray jsonArray = file.getAsJsonArray("extensions");
			for (JsonElement jsonElement : jsonArray) {
				builder.append(".appendExtension(").append(jsonElement.toString()).append(')');
			}
		}
		if (file.has("inputsInline")) {
			builder.append(".setInputsInline(").append(file.get("inputsInline").getAsBoolean()).append(")");
		}
		if (file.has("colour")) {
			builder.append(".setColor(").append(file.get("colour")).append(")");
		}
		if (file.has("output")) {
			JsonElement type = file.get("output");
			String type1 = buildTypesConvertor.apply(type.getAsString());
			builder.append(".setOutput(").append(type1).append(")");
		}
		if (file.has("previousStatement")) {
			builder.append(".setPreviousStatement(");
			if (!file.get("previousStatement").isJsonNull()) {
				builder.append("\"");
				builder.append(file.get("previousStatement").getAsString());
				builder.append("\"");
			} else {
				builder.append("null");
			}
			builder.append(")");
		}
		if (file.has("nextStatement")) {
			builder.append(".setNextStatement(");
			if (!file.get("previousStatement").isJsonNull()) {
				builder.append("\"");
				builder.append(file.get("nextStatement").getAsString());
				builder.append("\"");
			} else {
				builder.append("null");
			}
			builder.append(")");
		}
		if (file.has("mcreator")) {
			JsonObject mcreator = file.getAsJsonObject("mcreator");
			if (mcreator.has("toolbox_id")) {
				builder.append(".setToolBoxId(").append(mcreator.get("toolbox_id")).append(")");
			}
			if (mcreator.has("toolbox_init")) {
				var inits = mcreator.getAsJsonArray("toolbox_init");
				inits.forEach(jsonElement -> {
					builder.append(".appendToolBoxInit(\"").append(jsonElement.getAsString().replace("\"", "\\\""))
							.append("\")");
				});
			}
			if (mcreator.has("dependencies")) {
				JsonArray dependencies = mcreator.getAsJsonArray("dependencies");
				for (JsonElement jsonElement : dependencies) {
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					JsonElement type = jsonObject.get("type");
					BuiltInTypes builtInTypes = BuiltInTypes.getType(type.getAsString());
					String type1 = (builtInTypes == null) ? type.toString() : "BuiltInTypes." + builtInTypes.name();
					builder.append(".appendDependency(").append(jsonObject.get("name")).append(",").append(type1)
							.append(")");
				}
			}
			if (mcreator.has("group")) {
				builder.append(".setGroup(").append(mcreator.get("group")).append(")");
			}
			if (mcreator.has("required_api")) {
				JsonArray jsonArray = mcreator.getAsJsonArray("required_api");
				for (JsonElement jsonElement : jsonArray) {
					builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
				}
			}
		}
		return builder.toString();
	}

	public static String tryGenerateVariableCode(Path fl) {
		return tryGenerateVariableCode(loadStringFromFile(fl), getFileNameWithExtension(fl));
	}

	public static String tryGenerateVariableCode(String json, String fileName) {
		Gson gson = new Gson();
		var file = gson.fromJson(json, JsonObject.class);
		StringBuilder builder = new StringBuilder("pluginFactory.createVariable().setName(\"" + fileName + "\")");
		if (file.has("color")) {
			builder.append(".setColor(").append(file.get("color")).append(")");
		}
		if (file.has("blocklyVariableType")) {
			builder.append(".setBlocklyVariableType(").append(file.get("blocklyVariableType")).append(")");
		}
		if (file.has("nullable")) {
			builder.append(".setNullable(").append(file.get("nullable")).append(")");
		}
		if (file.has("ignoredByCoverage")) {
			builder.append(".setIgnoredByCoverage(").append(file.get("ignoredByCoverage")).append(")");
		}
		if (file.has("required_api")) {
			JsonArray jsonArray = file.getAsJsonArray("required_api");
			for (JsonElement jsonElement : jsonArray) {
				builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
			}
		}
		return builder.toString();
	}

	public static String tryGenerateTrigger(Path fl) {
		return tryGenerateTrigger(loadStringFromFile(fl), getFileNameWithExtension(fl));
	}

	public static String tryGenerateTrigger(String json, String fileName) {
		Gson gson = new Gson();
		var file = gson.fromJson(json, JsonObject.class);
		StringBuilder builder = new StringBuilder("pluginFactory.createTrigger().setName(\"" + fileName + "\")");
		if (file.has("side")) {
			builder.append(".setSide(Side.").append(Side.getSide(file.get("side").getAsString()).name()).append(")");
		}
		if (file.has("cancelable")) {
			builder.append(".setCancelable(").append(Boolean.parseBoolean(file.get("cancelable").getAsString()))
					.append(")");
		}
		if (file.has("has_result")) {
			builder.append(".setHasResult(").append(Boolean.parseBoolean(file.get("has_result").getAsString()))
					.append(")");
		}
		if (file.has("required_api")) {
			JsonArray jsonArray = file.getAsJsonArray("required_api");
			for (JsonElement jsonElement : jsonArray) {
				builder.append(".appendRequiredApi(").append(jsonElement.toString()).append(')');
			}
		}
		if (file.has("dependencies_provided")) {
			JsonArray jsonArray = file.getAsJsonArray("dependencies_provided");
			for (JsonElement jsonElement : jsonArray) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				JsonElement type = jsonObject.get("type");
				BuiltInTypes builtInTypes = BuiltInTypes.getType(type.getAsString());
				String type1 = (builtInTypes == null) ? type.toString() : "BuiltInTypes." + builtInTypes.name();
				builder.append(".appendDependency(").append(jsonObject.get("name")).append(",").append(type1)
						.append(')');
			}
		}
		return builder.toString();
	}

	@Contract("null->fail")
	public static String getFileNameWithExtension(Path file) {
		return file.toFile().getName().split("\\.")[0];
	}
}
