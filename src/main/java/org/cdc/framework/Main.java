package org.cdc.framework;

import org.cdc.framework.utils.FileUtils;
import org.cdc.framework.utils.parser.DefaultParameterConvertor;
import org.cdc.framework.utils.parser.MethodParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {
	public static void main(String[] args) throws IOException {
		var iterator = Arrays.asList(args).iterator();
		while (iterator.hasNext()) {
			var arg = iterator.next();
			if (arg.equals("--parseProcedure")) {
				if (iterator.hasNext()){
					var path = Path.of(iterator.next());
					if (Files.exists(path)){
						System.out.println(FileUtils.tryGenerateProcedureBuilderCode(path));
					}
					return;
				} else {
					System.err.println("You should specify the path after --parseProcedure");
					return;
				}
			} else if (arg.equals("--parseJava")){
				MethodParser methodParser = new MethodParser();
				if (iterator.hasNext()){
					var path = Path.of(iterator.next());
					if (Files.exists(path)){
						methodParser.parseClass(path.toFile());
						if (iterator.hasNext()){
							var methodName = iterator.next();
							methodParser.parseMethod(methodName);
							methodParser.setParameterStringFunction(new DefaultParameterConvertor());
							System.out.println(methodParser.toFTLContent());
							return;
						}
					}
				} else {
					System.err.println("You should specify the path after --parseJava");
					return;
				}
			}
		}
		System.err.println("--parseProcedure (Path)");
		System.err.println("--parseJava (JavaFilePath) (MethodName)");
	}
}
