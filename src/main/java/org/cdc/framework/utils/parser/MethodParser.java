package org.cdc.framework.utils.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.cdc.framework.utils.parser.annotation.Include;
import org.cdc.framework.utils.parser.annotation.StatementInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodParser implements TypeSolver {

	private TypeDeclaration<?> typeDeclaration;
	private MethodDeclaration methodDeclaration;
	private NodeList<ImportDeclaration> importDeclarationNodeList;
	private PackageDeclaration packageDeclaration;

	private TypeSolver parent;

	private Function<Parameter, String> parameterStringFunction;

	public MethodParser() {
		parameterStringFunction = new DefaultParameterConvertor();
	}

	public void parseClass(File javaFile) throws IOException {
		parseClass(Files.newInputStream(javaFile.toPath()));
	}

	public void parseClass(InputStream inputStream) {
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(new ReflectionTypeSolver());
		combinedTypeSolver.add(parent = new ClassLoaderTypeSolver(Thread.currentThread().getContextClassLoader()));
		combinedTypeSolver.add(this);
		parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
		JavaParser javaParser = new JavaParser(parserConfiguration);

		javaParser.parse(inputStream).ifSuccessful(compilationUnit -> {
			typeDeclaration = compilationUnit.getType(0);
			importDeclarationNodeList = compilationUnit.getImports();
			packageDeclaration = compilationUnit.getPackageDeclaration().orElse(null);
		});
	}

	public void parseMethod(String name, Class<?>... classes) {
		try {
			if (typeDeclaration == null) {
				return;
			}
			if (classes.length != 0) {
				String[] types = new String[classes.length];
				for (int index = 0; index < classes.length; index++) {
					types[index] = classes[index].getSimpleName();
				}
				methodDeclaration = typeDeclaration.getMethodsBySignature(name, types).getFirst();
			} else {
				methodDeclaration = typeDeclaration.getMethodsByName(name).getFirst();
			}
		} catch (NoSuchElementException e){
			throw new NoSuchElementException(name,e);
		}
	}

	public void setParameterStringFunction(Function<Parameter, String> parameterStringFunction) {
		this.parameterStringFunction = parameterStringFunction;
	}

	public String toFTLContent() {
		AtomicReference<String> atomicReference = new AtomicReference<>("");
		if (methodDeclaration != null) {
			methodDeclaration.getBody().ifPresent(blockStmt -> {

				blockStmt.accept(new VoidVisitorAdapter<Void>() {

					@Override public void visit(ExpressionStmt n, Void arg) {
						var ex = n.getExpression();
						if (ex.getChildNodes().getFirst() instanceof NameExpr _nameExpr) {
							var para = methodDeclaration.getParameterByName(_nameExpr.getNameAsString());
							if (para.isPresent()) {
								var pa = para.get();
								if (pa.isAnnotationPresent(StatementInput.class.getSimpleName())) {
									var parentNode = n.getParentNode();
									if (parentNode.isPresent()) {
										var statement = new ExpressionStmt(
												new NameExpr(parameterStringFunction.apply(pa)));
										parentNode.get().replace(n, statement);
										return;
									}
								}
							}
						}
						super.visit(n, arg);
					}

					@Override public void visit(NameExpr n, Void arg) {
						var converted = tryFindParameter(n);
						n.setName(converted);
						super.visit(n, arg);
					}
				}, null);
				String str = blockStmt.getStatements().size() == 1 ?
						blockStmt.getStatement(0).toString() :
						blockStmt.toString();
				str = str.lines().map(line -> {
					var lineTrim = line.trim();
					if (lineTrim.startsWith("${statement") && line.endsWith("};")) {
						return line.substring(0, line.length() - 1);
					} else if (lineTrim.startsWith("//")) {
						return line.replaceFirst("//", "");
					}
					return line;
				}).collect(Collectors.joining(System.lineSeparator()));
				atomicReference.set(str);
			});
			if (methodDeclaration.isAnnotationPresent(Include.class.getSimpleName())) {
				var annotation = methodDeclaration.getAnnotationByName(Include.class.getSimpleName());
				annotation.ifPresent(a -> a.accept(new VoidVisitorAdapter<Void>() {

					@Override public void visit(StringLiteralExpr n, Void arg) {
						var result = atomicReference.get();
						atomicReference.set("<#include \"%s\">".formatted(n.asString()).concat(System.lineSeparator())
								.concat(result));
						super.visit(n, arg);
					}
				}, null));
			}
		}
		return atomicReference.get();
	}

	private String tryFindParameter(NameExpr nameExpr) {
		var op = methodDeclaration.getParameterByName(nameExpr.getNameAsString());
		if (op.isPresent() && op.get().getAnnotations().isNonEmpty()) {
			return parameterStringFunction.apply(op.get());
		}
		return nameExpr.getNameAsString();
	}

	public TypeDeclaration<?> getTypeDeclaration() {
		return typeDeclaration.clone();
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration.clone();
	}

	@Override public TypeSolver getParent() {
		return parent;
	}

	@Override public void setParent(TypeSolver parent) {
	}

	@Override public SymbolReference<ResolvedReferenceTypeDeclaration> tryToSolveType(String name) {
		if (name.indexOf('.') == -1) {
			for (ImportDeclaration importDeclaration : importDeclarationNodeList) {
				if (importDeclaration.getNameAsString().endsWith("." + name)) {
					return parent.tryToSolveType(importDeclaration.getNameAsString());
				}
				if (importDeclaration.getNameAsString().endsWith(".*")) {
					try {
						var solved = parent.tryToSolveType(
								importDeclaration.getNameAsString().replaceFirst(".*$", "." + name));
						if (!solved.equals(SymbolReference.unsolved())) {
							return solved;
						}
					} catch (Exception ignored) {

					}
				}
			}
			if (packageDeclaration != null) {
				return parent.tryToSolveType(packageDeclaration.getNameAsString() + "." + name);
			}
		}
		return SymbolReference.unsolved();
	}
}
