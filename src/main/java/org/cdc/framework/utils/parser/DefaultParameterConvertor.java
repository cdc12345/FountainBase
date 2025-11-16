package org.cdc.framework.utils.parser;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.framework.utils.parser.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DefaultParameterConvertor implements Function<Parameter, String> {
	private List<String> originalPlaceHolder = new ArrayList<>();

	@Override public String apply(Parameter parameter) {
		String placeholder = getPlaceHolder(parameter);
		placeholder = placeholder.substring(2, placeholder.length() - 1);
		originalPlaceHolder.add(placeholder);
		String result = "%s";
		ResolvedTypeDeclaration type = null;
		try {
			type = parameter.getType().resolve().asReferenceType().getTypeDeclaration().orElse(null);
		} catch (Exception ignored) {
		}
		if (parameter.isAnnotationPresent(ItemStackCount.class)) {
			var annotation = parameter.getAnnotationByClass(ItemStackCount.class);
			final int[] count = { 1 };
			annotation.ifPresent(annotationExpr -> annotationExpr.accept(new VoidVisitorAdapter<Void>() {

				@Override public void visit(IntegerLiteralExpr n, Void arg) {
					count[0] = n.asNumber().intValue();
					super.visit(n, arg);
				}
			}, null));
			placeholder = "mappedMCItemToItemStackCode(%s, %d)".formatted(placeholder, count[0]);
		} else if (parameter.isAnnotationPresent(PlaceHolderModifier.class)) {
			final String[] template = { "%s" };
			var annotation = parameter.getAnnotationByClass(PlaceHolderModifier.class);
			annotation.ifPresent(annotationExpr -> annotationExpr.accept(new VoidVisitorAdapter<Void>() {

				@Override public void visit(StringLiteralExpr n, Void arg) {
					template[0] = n.asString();
					super.visit(n, arg);
				}
			}, null));
			placeholder = template[0].formatted(placeholder);
		}
		if (parameter.isAnnotationPresent(EnumLabel.class) || (type != null && type.isEnum())) {
			result = parameter.getTypeAsString() + ".%s";
		}
		return result.formatted("${" + placeholder + "}") ;
	}

	public static String getPlaceHolder(Parameter parameter) {
		if (parameter.isAnnotationPresent(StatementInput.class)) {
			return BuilderUtils.getStatementPlaceHolder(parameter.getNameAsString());
		} else if (parameter.isAnnotationPresent(Field.class)) {
			return BuilderUtils.getFieldPlaceHolder(parameter.getNameAsString());
		} else if (parameter.isAnnotationPresent(Input.class)) {
			return BuilderUtils.getInputPlaceHolder(parameter.getNameAsString());
		} else {
			return parameter.getNameAsString();
		}
	}
}
