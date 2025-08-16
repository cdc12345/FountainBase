package org.cdc.framework;

import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class FountainBaseMain {
	public static void main(String[] args) {
		ArrayList<MCreatorPluginFactory> mCreatorPluginFactoryArrayList = new ArrayList<>();
		if (args.length >= 1) {
			Stream.of(args).forEach(a->mCreatorPluginFactoryArrayList.add(MCreatorPluginFactory.createFactory(a)));
		}

		var iterator = mCreatorPluginFactoryArrayList.iterator();
		ServiceLoader<IFountainMain> serviceLoader = ServiceLoader.load(IFountainMain.class);
		serviceLoader.stream().forEach(a -> {
			MCreatorPluginFactory mCreatorPluginFactory1 = null;
			if (!iterator.hasNext()) {
				if (a.get().getClass().isAnnotationPresent(DefaultPluginFolder.class)) {
					mCreatorPluginFactory1 = new MCreatorPluginFactory(new File(a.get().getClass().getAnnotation(
							DefaultPluginFolder.class).value()));
				} else {
					mCreatorPluginFactory1 = mCreatorPluginFactoryArrayList.getLast();
				}
			} else {
				mCreatorPluginFactory1 = iterator.next();
			}
			a.get().generatePlugin(mCreatorPluginFactory1);
		});
	}
}
