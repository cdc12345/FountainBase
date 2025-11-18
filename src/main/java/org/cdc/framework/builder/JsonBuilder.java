package org.cdc.framework.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class JsonBuilder extends FileOutputBuilder<JsonElement> {
	protected JsonElement result;

	protected JsonBuilder(File rootPath, File targetPath) {
		super(rootPath, targetPath);
		this.fileExtension = "json";
	}

	@Override public JsonElement buildAndOutput() throws IOException {
		if (fileName == null) {
			throw new RuntimeException("filename can not be null!");
		}

		var json = build();
		if (targetPath != null) {
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
			var file = new File(targetPath, getFileFullName());
			System.out.println(file.getPath());
			Files.copy(new ByteArrayInputStream(gson.toJson(json).getBytes(StandardCharsets.UTF_8)), file.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}
		return json;
	}
}
