package org.cdc.test;

import org.cdc.framework.utils.parser.annotation.*;

import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class ParsedClass {
	@Include({ "mcitems.ftl" })
	private void parsedMethod(@Input String entity, @Field String entity1, @StatementInput Runnable runnable,
			@StatementInput Runnable runnable1, @Input StandardCopyOption copyOption, @ItemStackCount(2) @Input String itemStack,
			@PlaceHolderModifier("method(%s)") String testTemplate) {
		entity.equals(entity1);
		new Thread(() -> {
			runnable.run();
		});

		new Thread(() -> {
			runnable1.run();
		});
		if (true) {
			//<#if eseseses>
			System.out.println(copyOption);
			System.out.println(itemStack);
			System.out.println(testTemplate);
			//</#if>
		}
	}

	private void hey_set(String hello, Object placeholder, String iterator, @StatementInput Consumer statement) {
		System.out.println(hello);
		if (true) {
			statement.accept(null);
		}
	}

	private void singleLineMethod(String entity, String entity1) {
		if (entity.equals(entity1)) {
			System.out.println(entity);
		}
	}
}
