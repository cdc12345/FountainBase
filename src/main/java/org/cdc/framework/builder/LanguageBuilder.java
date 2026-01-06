package org.cdc.framework.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.cdc.framework.interfaces.IVariableType;
import org.cdc.framework.utils.L10NHelper;
import org.jetbrains.annotations.PropertyKey;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LanguageBuilder extends FileOutputBuilder<Properties> {

	private static final String DEFAULT_BUNDLE = "lang.texts";

	private final Properties result;
	private boolean flagToOutput;

	public LanguageBuilder(File rootPath, String fileName) {
		super(rootPath, new File(rootPath, "lang"));
		this.result = new Properties();

		this.fileName = fileName;
		this.fileExtension = "properties";

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (!flagToOutput) {
				System.err.println("Attention, Your language do not output!! This may crash your mcreator");
			}
		}));

		loadModifier();
	}

	public LanguageBuilder loadModifier(){
		load();
		return this;
	}

	/**
	 * 表意不明，不建议使用
	 * @return this
	 */
	@Deprecated
	public LanguageBuilder load() {
		try {
			var file = new File(targetPath, fileName + "." + fileExtension + ".modifier");
			if (file.exists())
				this.result.load(new FileReader(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public LanguageBuilder clear() {
		result.clear();
		return this;
	}

	/**
	 * blockly.block.atomic_itemstack_set=Set itemstack atomic %1 to %2
	 *
	 * @param key   key
	 * @param localizedText value
	 * @return this
	 */
	public LanguageBuilder appendLocalization(@SuppressWarnings("UnresolvedPropertyKey") @PropertyKey(resourceBundle = DEFAULT_BUNDLE) String key, String localizedText) {
		this.result.setProperty(key, localizedText);
		return this;
	}

	/**
	 * blockly.block.atomic_itemstack_set=Set itemstack atomic %1 to %2
	 *
	 * @param proName procedure's name
	 * @param localizedText   localizedText
	 * @return this
	 */
	public LanguageBuilder appendProcedure(String proName, String localizedText) {
		return appendLocalization(L10NHelper.getProcedureKey(proName), localizedText.trim());
	}

	public LanguageBuilder appendTrigger(String triggerName, String localizedText) {
		return appendLocalization(L10NHelper.getTriggerKey(triggerName), localizedText.trim());
	}

	public LanguageBuilder appendProcedureToolTip(String proName, String localizedText) {
		return appendLocalization(L10NHelper.getProcedureToolTipKey(proName), localizedText.trim());
	}

	public LanguageBuilder appendProcedureCategory(String category, String localizedText) {
		return appendLocalization(L10NHelper.getProcedureCategoryKey(category), localizedText.trim());
	}

	@CanIgnoreReturnValue public LanguageBuilder appendWarning(String warningKey, String localizedText) {
		return appendLocalization(L10NHelper.getWarningKey(warningKey), localizedText);
	}

	public LanguageBuilder appendCustomVariableDependency(IVariableType variable, String localizedText) {
		return appendLocalization(L10NHelper.getCustomVariableDependencyKey(variable.getVariableType()), localizedText);
	}

	public LanguageBuilder appendDataListMessage(String datalistName, String localizedText) {
		return appendLocalization(L10NHelper.getDataListKey(datalistName), localizedText);
	}

	@Override public Properties build() {
		return null;
	}

	/**
	 * 在所有语言文件设置完成后调用
	 * This should be called after all entries appended
	 * @return result
	 */
	@Override public Properties buildAndOutput() {
		try {
			flagToOutput = true;
			this.result.store(new FileWriter(new File(targetPath, getFileFullName()), StandardCharsets.UTF_8),
					"Auto-Generated");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this.result;
	}
}
