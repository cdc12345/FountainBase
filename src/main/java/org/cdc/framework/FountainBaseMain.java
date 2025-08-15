package org.cdc.framework;

import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class FountainBaseMain {
	public static void main(String[] args) {
		AtomicReference<MCreatorPluginFactory> mCreatorPluginFactoryAtomicReference = new AtomicReference<>(null);
		if (args.length >= 1) {
			Path pluginDirectory = Paths.get(args[0]);
			mCreatorPluginFactoryAtomicReference.set(new MCreatorPluginFactory(pluginDirectory.toFile()));
		}

		ServiceLoader<IFountainMain> serviceLoader = ServiceLoader.load(IFountainMain.class);
		serviceLoader.stream().forEach(a -> {
			MCreatorPluginFactory mCreatorPluginFactory1 = null;
			if (mCreatorPluginFactoryAtomicReference.get() == null) {
				if (a.get().getClass().isAnnotationPresent(DefaultPluginFolder.class)) {
					mCreatorPluginFactory1 = new MCreatorPluginFactory(new File(a.get().getClass().getAnnotation(
							DefaultPluginFolder.class).value()));
				}
			} else {
				mCreatorPluginFactory1 = mCreatorPluginFactoryAtomicReference.get();
			}
			a.get().generatePlugin(mCreatorPluginFactory1);
		});
	}
}
