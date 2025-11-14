package org.cdc.framework.builder;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IGeneratorInit;
import org.cdc.framework.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.cdc.framework.utils.yaml.YamlDataUtils.keyAndValue;
import static org.cdc.framework.utils.yaml.YamlDataUtils.str;

public class DataListBuilder extends FileOutputBuilder<Map<String, String>> implements IGeneratorInit {
	private final String DEFAULT_KEY = "_default";

	private final Map<String, String> result;

	public Consumer<Void> redo;

	private Flags flags = new Flags();

	private static class Flags {
		protected boolean flagToInitGenerator;
	}

	public DataListBuilder(File rootPath) {
		super(rootPath, new File(rootPath, "datalists"));
		result = new LinkedHashMap<>();
		this.fileExtension = "yaml";
		this.redo = a -> {};
	}

	public DataListBuilder setName(String name) {
		this.fileName = FileUtils.filterSpace(name);
		return this;
	}

	public DataListBuilder appendElement(String element) {
		return appendElement(element, element);
	}

	public DataListBuilder appendElement(String element, String defaultMapping) {
		result.put(element, defaultMapping);
		redo = a -> result.remove(element);
		return this;
	}

	public DataListBuilder appendElement(String element, List<String> map) {
		return appendElement(element, Collections.emptyMap(), map);
	}

	public DataListBuilder appendElement(String element, String readableName, List<String> map) {
		return appendElement(element, Map.of("readable_name", readableName), map);
	}

	/**
	 * @param element              element name
	 * @param definitionMap        like Map.of("readable_name","he")
	 * @param defaultListInMapping default list
	 * @return this
	 */
	public DataListBuilder appendElement(String element, Map<String, String> definitionMap,
			List<String> defaultListInMapping) {
		var elementResult = element + (!definitionMap.isEmpty() ?
				':' + System.lineSeparator() + "  " + definitionMap.entrySet().stream()
						.map(entry -> keyAndValue(entry.getKey(), str(entry.getValue())))
						.collect(Collectors.joining(System.lineSeparator() + "  ")) :
				"");

		if (defaultListInMapping.isEmpty()) {
			defaultListInMapping = new ArrayList<>();
			defaultListInMapping.add("");
		}
		String addition1;
		appendElement(elementResult, defaultListInMapping.size() <= 1 ?
				defaultListInMapping.getFirst() :
				System.lineSeparator() + " - " + (addition1 = defaultListInMapping.toString()).substring(1,
						addition1.length() - 1).replace(",", System.lineSeparator() + " -"));
		return this;
	}

	public DataListBuilder setDefault() {
		return setDefault(result.values().stream().findFirst().orElse(null));
	}

	public DataListBuilder setDefault(String defaultMapping) {
		return appendElement(DEFAULT_KEY, defaultMapping);
	}

	/**
	 * @param mapTemplate _mcreator_map_template
	 * @return this
	 */
	public DataListBuilder setMapTemplate(String mapTemplate) {
		String MAP_TEMPLATE = "_mcreator_map_template";
		return appendElement(MAP_TEMPLATE, "\"" + mapTemplate + "\"");
	}

	@Override public Map<String, String> build() {
		return result;
	}

	@Override public Map<String, String> buildAndOutput() {
		if (fileName == null) {
			throw new RuntimeException("filename can not be null");
		}
		var build1 = build();
		try {
			var build = build1.keySet().stream().filter(a -> !a.startsWith("_")).toList();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("- ").append(build.getFirst());
			build.stream().skip(1).forEach(a -> stringBuilder.append(System.lineSeparator()).append("- ").append(a));
			Files.copy(new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)),
					new File(targetPath, getFileFullName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return build1;
	}

	public DataListBuilder setMessageLocalization(LanguageBuilder languageBuilder, String value) {
		languageBuilder.appendDataListMessage(this.fileName, value);
		return this;
	}

	public DataListBuilder initGenerator() {
		flags.flagToInitGenerator = true;
		return this;
	}

	@Override public void initGenerator0(String generatorName, boolean replace) {
		if (fileName == null) {
			return;
		}
		TreeMap<String, String> hashMap = new TreeMap<>();
		for (Map.Entry<String, String> entry : result.entrySet()) {
			if (entry.getKey().contains(": ")) {
				hashMap.put(entry.getKey().substring(0, entry.getKey().indexOf(':')), entry.getValue());
			} else {
				hashMap.put(entry.getKey(), entry.getValue());
			}
		}
		var generator1 = Paths.get(rootPath.getPath(), generatorName, "mappings", getFileFullName());
		try {
			System.out.println(generator1);
			String mapString;
			var source = new ByteArrayInputStream(
					(mapString = hashMap.toString()).substring(1).substring(0, mapString.length() - 2)
							.replace("=", ": ").replace(", ", System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
			if (replace) {
				Files.copy(source, generator1, StandardCopyOption.REPLACE_EXISTING);
			} else {
				Files.copy(source, generator1);
			}
		} catch (IOException ignored) {
		}
	}

	@Override public boolean isSupported(MCreatorPluginFactory mCreatorPluginFactory) {
		return flags.flagToInitGenerator && mCreatorPluginFactory.rootPath().equals(rootPath);
	}
}
