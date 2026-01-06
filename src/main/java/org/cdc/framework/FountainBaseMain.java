package org.cdc.framework;

import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class FountainBaseMain {
	public static void main(String[] args) {
		ArrayList<MCreatorPluginFactory> mCreatorPluginFactoryArrayList = new ArrayList<>();
		HashMap<String, MCreatorPluginFactory> pathToFactory = new HashMap<>();
		if (args.length >= 1) {
			Stream.of(args).forEach(a -> {
				if (!pathToFactory.containsKey(a)) {
					var factory = MCreatorPluginFactory.createFactory(a);
					pathToFactory.put(a,factory);
					mCreatorPluginFactoryArrayList.add(factory);
				} else {
					mCreatorPluginFactoryArrayList.add(pathToFactory.get(a));
				}
			});

		}

		var iterator = mCreatorPluginFactoryArrayList.iterator();
		ServiceLoader<IFountainMain> serviceLoader = ServiceLoader.load(IFountainMain.class);
		serviceLoader.stream().forEach(a -> {
			MCreatorPluginFactory mCreatorPluginFactory1;
			//build the MCreatorPluginFactory
			if (!iterator.hasNext()) {
				if (a.get().getClass().isAnnotationPresent(DefaultPluginFolder.class)) {
					var target = a.get().getClass().getAnnotation(DefaultPluginFolder.class).value();

					if (pathToFactory.containsKey(target)){
						mCreatorPluginFactory1 = pathToFactory.get(target);
					} else {
						mCreatorPluginFactory1 = MCreatorPluginFactory.createFactory(target);
					}
				} else {
					mCreatorPluginFactory1 = mCreatorPluginFactoryArrayList.getLast();
				}
			} else {
				mCreatorPluginFactory1 = iterator.next();
			}
			try {
				a.get().generatePluginInfo(mCreatorPluginFactory1.createInfo());
				a.get().generatePlugin(mCreatorPluginFactory1);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
