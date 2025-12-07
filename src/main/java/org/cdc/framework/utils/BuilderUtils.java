package org.cdc.framework.utils;

import com.google.gson.JsonArray;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.ProcedureBuilder;
import org.cdc.framework.interfaces.IProcedureCategory;
import org.cdc.framework.interfaces.IVariableType;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.cdc.framework.utils.yaml.YamlDataUtils.*;

public class BuilderUtils {

	public static ProcedureBuilder createOutputProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name,
			String output) {
		return mCreatorPluginFactory.createProcedure(name).setOutput(output).setGroup("name").setInputsInline(true);
	}

	/**
	 * mCreatorPluginFactory.createProcedure(name).setOutput(variableType).setGroup("name").setInputsInline(true)
	 */
	public static ProcedureBuilder createOutputProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name,
			IVariableType variableType) {
		return mCreatorPluginFactory.createProcedure(name).setOutput(variableType).setGroup("name")
				.setInputsInline(true);
	}

	/**
	 * .createProcedure(name).setGroup("name").setPreviousStatement(null).setNextStatement(null).setInputsInline(true)
	 */
	public static ProcedureBuilder createCommonProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name) {
		return mCreatorPluginFactory.createProcedure(name).setGroup("name").setPreviousStatement(null)
				.setNextStatement(null).setInputsInline(true);
	}

	public static ProcedureBuilder createEndProcedure(MCreatorPluginFactory mCreatorPluginFactory, String name) {
		return mCreatorPluginFactory.createProcedure(name).setGroup("name").setPreviousStatement(null)
				.setInputsInline(true);
	}

	public static ProcedureBuilder createProcedureCategory(MCreatorPluginFactory mCreatorPluginFactory,
			IProcedureCategory category) {
		return createProcedureCategory(mCreatorPluginFactory, category.getName());
	}

	public static ProcedureBuilder createProcedureCategory(MCreatorPluginFactory mCreatorPluginFactory, String name) {
		var aitaskscat = mCreatorPluginFactory.createProcedure();
		if (name != null) {
			aitaskscat.setName(name);
		}
		aitaskscat.markType();
		return aitaskscat;
	}

	public static ProcedureBuilder createAITaskCategory(MCreatorPluginFactory mCreatorPluginFactory, String category) {
		var pro = mCreatorPluginFactory.createAITask();
		pro.markType();
		if (category != null) {
			pro.setName(category);
		}
		return pro;
	}

	public static ProcedureBuilder createProcedureWithStatement(MCreatorPluginFactory mCreatorPluginFactory,
			String name, String statementName, IVariableType placeHolderType, String statementProviderName,
			IVariableType statementProviderType) {
		return mCreatorPluginFactory.createProcedure(name).appendArgs0StatementInput(statementName)
				.appendArgs0InputValue("_placeholder", placeHolderType).setGroup("name").setPreviousStatement(null)
				.setNextStatement(null).statementBuilder().setName(statementName)
				.appendProvide(statementProviderName, statementProviderType).buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED);
	}

	public static int countLanguageParameterCount(String text) {
		Pattern var = Pattern.compile("%\\d");
		var ma = var.matcher(text);
		int count = 0;
		while (ma.find()) {
			count++;
		}
		return count;
	}

	public static String getInputPlaceHolder(String name) {
		return "${input$" + name + "}";
	}

	public static String getStatementPlaceHolder(String name) {
		return "${statement$" + name + "}";
	}

	public static String getFieldPlaceHolder(String name) {
		return "${field$" + name + "}";
	}

	public static boolean isSupportProcedure(String generatorName) {
		return generatorName.startsWith("forge") || generatorName.startsWith("neoforge") || generatorName.startsWith(
				"fabric") || generatorName.startsWith("spigot");
	}

	public static String generateInputsComment(JsonArray inputs) {
		return inputs.asList().stream().map(a -> getInputPlaceHolder(a.getAsString()))
				.collect(Collectors.joining(",", "<#-- ", " -->"));
	}

	public static String generateStatementsComment(JsonArray statements) {
		return statements.asList().stream()
				.map(a -> getStatementPlaceHolder(a.getAsJsonObject().get("name").getAsString()))
				.collect(Collectors.joining(",", "<#-- ", " -->"));
	}

	public static String generateFieldsComment(JsonArray fields) {
		return fields.asList().stream().map(a -> getFieldPlaceHolder(a.getAsString()))
				.collect(Collectors.joining(",", "<#-- ", " -->"));
	}

	public static String generateTriggerDependencies(Map<String, String> dependencies) {
		String mapCode = dependencies.entrySet().stream()
				.map(entry -> keyAndValue(str(entry.getKey()),str(entry.getValue())))
				.collect(Collectors.joining(", " + lineSeparator + "\t\t\t"));
		return String.format("""
						<#assign dependenciesCode><#compress>
							<@procedureDependenciesCode dependencies, {
							%s
							}/>
						</#compress></#assign>
						execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
				""", mapCode);
	}
}
