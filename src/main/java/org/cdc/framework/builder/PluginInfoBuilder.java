package org.cdc.framework.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cdc.framework.utils.MCreatorVersions;

import java.io.File;

public class PluginInfoBuilder extends JsonBuilder{

	private final JsonArray supportedVersion;
	private final JsonObject info;

	public PluginInfoBuilder(File rootPath) {
		super(rootPath, rootPath);

		this.result = new JsonObject();
		this.supportedVersion = new JsonArray();
		this.info = new JsonObject();
		this.fileName = "plugin";
	}

	public PluginInfoBuilder setId(String id){
		this.result.getAsJsonObject().addProperty("id",id);
		return this;
	}

	public PluginInfoBuilder setWeight(int weight){
		this.result.getAsJsonObject().addProperty("weight",weight);
		return this;
	}

	public PluginInfoBuilder addSupportedVersion(String version){
		this.supportedVersion.add(MCreatorVersions.toFormattedVersion(version));
		return this;
	}

	public PluginInfoBuilder setName(String name){
		this.info.addProperty("name",name);
		return this;
	}

	public PluginInfoBuilder setVersion(String version){
		this.info.addProperty("version",version);
		return this;
	}

	public PluginInfoBuilder setDescription(String description){
		this.info.addProperty("description",description);
		return this;
	}

	public PluginInfoBuilder setAuthor(String author){
		this.info.addProperty("author",author);
		return this;
	}

	@Override public JsonElement build() {
		if (!result.getAsJsonObject().has("id")){
			throw new RuntimeException("id is null");
		}
		if (!supportedVersion.isEmpty()){
			this.result.getAsJsonObject().add("supportedversions",supportedVersion);
		}
		if (!info.isEmpty()){
			this.result.getAsJsonObject().add("info",info);
			if (!info.has("name")){
				setName(result.getAsJsonObject().get("id").getAsString());
			}
		}
		return this.result;
	}
}
