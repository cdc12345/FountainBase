package org.cdc.framework.interfaces;

import org.cdc.framework.MCreatorPluginFactory;

@FunctionalInterface
public interface IFountainMain {
	void generatePlugin(MCreatorPluginFactory pluginFactory);
}
