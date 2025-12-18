package org.cdc.test;

import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class ParsedClass {
	/*
%0
	<#if eseseses>
%1
	</#if>
	 */
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
			// Divide
			System.out.println(copyOption);
			System.out.println(itemStack);
			System.out.println(testTemplate);
		}
	}

	private void hey_set(String hello, Object placeholder, String iterator, @StatementInput Consumer statement) {
		System.out.println(hello);
		if (true) {
			statement.accept(null);
		}
	}

	/*
<@head>%0</@head>
%1
<@tail>%2</@tail>
 	*/
	@Include({ "mcitems.ftl" })
	private void singleLineMethod(String entity, String entity1) {
		if (entity.equals(entity1)) {
			// Divide
			System.out.println(entity);
			// Divide
		}
	}

	private void creativetab_insertafter(Event event,@Input @ItemStackCount ItemStack after,@Input @ItemStackCount ItemStack item,
			@Field @EnumLabel CreativeModeTab.TabVisibility tabvisible) {
		if (event instanceof BuildCreativeModeTabContentsEvent _event) {
			_event.insertAfter(after, item, tabvisible);
		}
	}
}
