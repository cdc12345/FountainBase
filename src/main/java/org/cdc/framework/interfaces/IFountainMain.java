package org.cdc.framework.interfaces;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.PluginInfoBuilder;

@FunctionalInterface public interface IFountainMain {

	default void generatePluginInfo(PluginInfoBuilder infoBuilder) {}

	void generatePlugin(MCreatorPluginFactory pluginFactory);
}
