package org.cdc.framework.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.cdc.framework.interfaces.IVariableType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LanguageBuilder extends FileOutputBuilder<Properties> {

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
	 * @param value value
	 * @return this
	 */
	public LanguageBuilder appendLocalization(String key, String value) {
		this.result.setProperty(key, value);
		return this;
	}

	/**
	 * blockly.block.atomic_itemstack_set=Set itemstack atomic %1 to %2
	 *
	 * @param proName procedure's name
	 * @param value   value
	 * @return this
	 */
	public LanguageBuilder appendProcedure(String proName, String value) {
		return appendLocalization("blockly.block." + proName, value.trim());
	}

	public LanguageBuilder appendTrigger(String triggerName, String value) {
		return appendLocalization("trigger." + triggerName, value.trim());
	}

	public LanguageBuilder appendProcedureToolTip(String proName, String value) {
		return appendLocalization("blockly.block." + proName + ".tooltip", value.trim());
	}

	public LanguageBuilder appendProcedureCategory(String category, String value) {
		return appendLocalization("blockly.category." + category, value.trim());
	}

	@CanIgnoreReturnValue public LanguageBuilder appendWarning(String warningKey, String value) {
		return appendLocalization("blockly.warning." + warningKey, value);
	}

	public LanguageBuilder appendCustomVariableDependency(IVariableType variable, String value) {
		return appendLocalization("blockly.block.custom_dependency_" + variable.getVariableType(), value);
	}

	public LanguageBuilder appendDataListMessage(String datalistName, String value) {
		return appendLocalization("dialog.selector." + datalistName + ".message", value);
	}

	@Override public Properties build() {
		return null;
	}

	/**
	 * 在所有语言文件设置完成后调用
	 *
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
