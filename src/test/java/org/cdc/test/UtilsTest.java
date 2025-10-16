package org.cdc.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import org.cdc.framework.utils.ColorUtils;
import org.cdc.framework.utils.FileUtils;
import org.cdc.framework.utils.MCreatorVersions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class UtilsTest {
	@Test public void testHueColor() {
		Assertions.assertEquals(ColorUtils.colorHue("helloworld"), "%{BKY_helloworld_HUE}");
	}

	@Test public void testGenerateCode() throws URISyntaxException {
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		JavaParser javaParser = new JavaParser(parserConfiguration);
		var testProcedure = FileUtils.tryGenerateProcedureBuilderCode(
				Path.of(this.getClass().getResource("/arraylist_foreach.json").toURI()))+";";
		System.out.println(testProcedure);
		var problems = javaParser.parseStatement(testProcedure).getProblems();
		Assertions.assertTrue(problems.isEmpty());

	}

	@Test public void testFileName() {
		Assertions.assertEquals(FileUtils.filterSpace("hello world"), "hello_world");
	}

	@Test public void testVersions() {
		Assertions.assertEquals("2025001", MCreatorVersions.toFormattedVersion(MCreatorVersions.V_2025_1));
		Assertions.assertEquals("202500199999", MCreatorVersions.toFormattedVersion(MCreatorVersions.V_2025_1_9999));
		Assertions.assertEquals("202500399999", MCreatorVersions.toDevelopingVersion(MCreatorVersions.V_2025_3));
	}
}
