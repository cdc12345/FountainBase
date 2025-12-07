package org.cdc.framework.interfaces;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.PluginInfoBuilder;

import java.io.IOException;

@FunctionalInterface public interface IFountainMain {

	default void generatePluginInfo(PluginInfoBuilder infoBuilder) throws IOException {}

	void generatePlugin(MCreatorPluginFactory pluginFactory) throws IOException;
}
