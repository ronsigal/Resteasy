package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionClassDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

/**
 * Some code that uses JavaParser.
 */
public class JavaToProtobufGenerator {

	private static Map<String, String> TYPE_MAP = new HashMap<String, String>();
	private static Set<String> ANNOTATIONS = new HashSet<String>();
	private static Set<String> HTTP_VERBS = new HashSet<String>();
	private static Map<String, String> PRIMITIVE_WRAPPERS = new HashMap<String, String>();
	private static Map<String, String> PRIMITIVE_WRAPPER_TYPES = new HashMap<String, String>();
	private static boolean needEmpty = false;
	private static List<ResolvedReferenceTypeDeclaration> resolvedTypes = new CopyOnWriteArrayList<ResolvedReferenceTypeDeclaration>();
	private static Set<String> visited = new HashSet<String>();
	private static JavaSymbolSolver symbolSolver;
	private static ClassVisitor classVisitor = new ClassVisitor();
	private static int counter = 1;

	static {
		TYPE_MAP.put("byte", "int32");
		TYPE_MAP.put("short", "int32");
		TYPE_MAP.put("int", "int32");
		TYPE_MAP.put("long", "int64");
		TYPE_MAP.put("float", "float");
		TYPE_MAP.put("double", "double");
		TYPE_MAP.put("boolean", "bool");
		TYPE_MAP.put("char", "int32");
		TYPE_MAP.put("String", "string");

		ANNOTATIONS.add("Context");
		ANNOTATIONS.add("CookieParam");
		ANNOTATIONS.add("HeaderParam");
		ANNOTATIONS.add("MatrixParam");
		ANNOTATIONS.add("PathParam");
		ANNOTATIONS.add("QueryParam");

		HTTP_VERBS.add("DELETE");
		HTTP_VERBS.add("HEAD");
		HTTP_VERBS.add("GET");
		HTTP_VERBS.add("OPTIONS");
		HTTP_VERBS.add("PATCH");
		HTTP_VERBS.add("POST");
		HTTP_VERBS.add("PUT");

		PRIMITIVE_WRAPPER_TYPES.put("short",   "Short");
		PRIMITIVE_WRAPPER_TYPES.put("int",     "Integer");
		PRIMITIVE_WRAPPER_TYPES.put("long",    "Long");
		PRIMITIVE_WRAPPER_TYPES.put("float",   "Float");
		PRIMITIVE_WRAPPER_TYPES.put("double",  "Double");
		PRIMITIVE_WRAPPER_TYPES.put("boolean", "Boolean");
		PRIMITIVE_WRAPPER_TYPES.put("char",    "Char");
		PRIMITIVE_WRAPPER_TYPES.put("string",  "String");
		
		PRIMITIVE_WRAPPERS.put("Short",   "message Short   {int32  value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Integer", "message Integer {int32  value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Long",    "message Long    {int64  value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Float",   "message Float   {float  value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Double",  "message Double  {double value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Boolean", "message Boolean {bool   value = $V$;}");
		PRIMITIVE_WRAPPERS.put("Char",    "message Char    {int32  value = $V$;}");
		PRIMITIVE_WRAPPERS.put("String",  "message String  {string value = $V$;}");
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 4) {
			System.out.println("need four args");
			System.out.println("  arg[0]: java file");
			System.out.println("  arg[1]: package to be used in .proto file");
			System.out.println("  arg[2]: java package to be used in .proto file");
			System.out.println("  arg[3]: java outer classname to be generated from .proto file");
			return;
		}
		StringBuilder sb = new StringBuilder();
		protobufHeader(args, sb);
		new JavaToProtobufGenerator().processClasses(args, sb);
		while (!resolvedTypes.isEmpty()) {
			for (ResolvedReferenceTypeDeclaration rrtd : resolvedTypes) {
				classVisitor.visit(rrtd, sb);
			}
		}
		finishProto(sb);
		writeProtoFile(args, sb);
	}

	private static void protobufHeader(String[] args, StringBuilder sb)
	{
		sb.append("syntax = \"proto3\";\n");
		sb.append("package " + args[1] + ";\n");
		sb.append("option java_package = \"" + args[2] + "\";\n");
		sb.append("option java_outer_classname = \"" + args[3] + "_proto\";\n");
	}
	
	private static void finishProto(StringBuilder sb) {
		if (needEmpty) {
			sb.append("\nmessage Empty {}");
		}
		
		for (String wrapper : PRIMITIVE_WRAPPERS.values()) {
			sb.append("\n").append(wrapper.replace("$V$", String.valueOf(counter++)));
		}
	}

	private void processClasses(String[] args, StringBuilder sb) throws IOException {
		Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

		// SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
		// In this case the root directory is found by taking the root from the current Maven module,
		// with src/main/resources appended.
		SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(JavaToProtobufGenerator.class).resolve("src/main/resources"));
		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
		TypeSolver javaParserTypeSolver = new JavaParserTypeSolver("src/main/resources");
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(reflectionTypeSolver);
		combinedTypeSolver.add(javaParserTypeSolver);
		symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
		sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
		CompilationUnit cu = sourceRoot.parse("", args[0]);
		classVisitor.visit(cu, sb);
	}

	static private void writeProtoFile(String[] args, StringBuilder sb) throws IOException {
		File dir = new File("target/generatedSources/");
		if(!dir.exists()){
			dir.mkdir();
		} 
		File file = new File("target/generatedSources/" + args[3] + ".proto");
		file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
	}

	static class ClassVisitor extends VoidVisitorAdapter<StringBuilder> {

		@Override
		/*
		 * Visit classes in configured files.
		 */
		public void visit(final ClassOrInterfaceDeclaration subClass, StringBuilder sb)
		{
			//			if (subClass.isInterface()) {
			//				return;
			//			}
			ResolvedReferenceTypeDeclaration rrtd = subClass.resolve();
			String fqn = rrtd.getPackageName() + "." + rrtd.getClassName();
			if (visited.contains(fqn)) {
				return;
			}
			visited.add(fqn);
			
			// Begin protobuf message definition.
			sb.append("\nmessage ").append(fqnify(fqn)).append(" {\n");
			
			// Scan all variables in class.
			for (BodyDeclaration<?> bd: subClass.getMembers()) {
				if (bd instanceof FieldDeclaration) {
					FieldDeclaration fd = (FieldDeclaration) bd;
					for (VariableDeclarator vd : fd.getVariables()) {
						String type = TYPE_MAP.get(vd.getType().getElementType().asString());
						if (type != null) { // built-in type
							if (vd.getType().isArrayType()) {
								type = "repeated " + TYPE_MAP.get(vd.getType().getElementType().asString());
							} else {
								type = TYPE_MAP.get(vd.getType().asString());
							}
						} else { // Defined message type
							ReferenceTypeImpl rt = (ReferenceTypeImpl) vd.getType().resolve();
							rrtd = rt.getTypeDeclaration().get();
							type = fqnify(removeTypeVariables(rt.asReferenceType().getQualifiedName()));
							if (!visited.contains(type)) {
								resolvedTypes.add(rrtd);
							}
						}
						sb.append("  ")
						.append(type)
						.append(" ")
						.append(vd.getNameAsString())
						.append(" = ")
						.append(counter++)
						.append(";\n");
					}
				}
			}
			// Add field for superclass.
			for (Node node : subClass.getExtendedTypes()) {
				if (node instanceof ClassOrInterfaceType) {
					ClassOrInterfaceType coit = (ClassOrInterfaceType) node;
					ResolvedReferenceType rrt = coit.resolve();
					rrtd = rrt.getTypeDeclaration().get();
					if (rrtd instanceof JavaParserClassDeclaration) {
						JavaParserClassDeclaration jpcd = (JavaParserClassDeclaration) rrtd;
						ClassOrInterfaceDeclaration superClass = jpcd.getWrappedNode();
						String packageName = fqnify(jpcd.getPackageName());
						String superClassName = superClass.getNameAsString();
						String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1));
						sb.append("  ")
						.append(packageName)
						.append("_")
						.append(superClassName)
						.append(" ")
						.append(superClassVariableName)
						.append(" = ")
						.append(counter++)
						.append(";\n");
					}
				}
			}
			sb.append("}\n");
			
			// Add service with a method for each resource method in class.
			boolean started = false;
			for (BodyDeclaration<?> bd : subClass.getMembers()) {
				if (bd instanceof MethodDeclaration) {
					MethodDeclaration md = (MethodDeclaration) bd;
					if (!isResourceMethod(md)) {
						continue;
					}
					if (!started) {
						sb.append("\nservice ")
						.append(fqnify(subClass.getNameAsString()))
						.append("Service {\n");
						started = true;
					}
					sb.append("  rpc ")
					.append(md.getNameAsString())
					.append(" (")
					.append(getEntityParameter(md))
					.append(") returns (")
					.append(getReturnType(md))
					.append(");\n");
				}
			}
			if (started) {
				sb.append("}\n");
			}
		}
		
		/*
		 * Visit classes discovered by JavaParserTypeSolver.
		 */
		public void visit(ResolvedReferenceTypeDeclaration clazz, StringBuilder sb) {
			resolvedTypes.remove(clazz);
			String fqn = clazz.getPackageName() + "." + clazz.getClassName();
			if (visited.contains(fqn)) {
				return;
			}
			visited.add(fqn);

			//			if (subClass.isInterface()) {
			//				return;
			//			}
			
			// Begin protobuf message definition.
			sb.append("\nmessage ").append(fqnify(fqn)).append(" {\n");
			
			// Scan all variables in class.
			for (ResolvedFieldDeclaration rfd: clazz.getDeclaredFields()) {
				String type = null;
				if (rfd.getType().isPrimitive()) { // Built-in type
					type = TYPE_MAP.get(rfd.getType().describe());
				}  else if (rfd.getType() instanceof ResolvedArrayType) {
					ResolvedArrayType rat = (ResolvedArrayType) rfd.getType();
					ResolvedType ct = rat.getComponentType();
					if (ct.isPrimitive()) {
						type = "repeated " + TYPE_MAP.get(removeTypeVariables(ct.describe()));
					} else {
						fqn = removeTypeVariables(ct.describe());
						if (!visited.contains(fqn)) {
							resolvedTypes.add(ct.asReferenceType().getTypeDeclaration().get());
						}
						type = "repeated " + fqnify(fqn);
					}
				} else { // Defined type
					if (rfd.getType().isReferenceType()) {
						ResolvedReferenceTypeDeclaration rrtd = (ResolvedReferenceTypeDeclaration) rfd.getType().asReferenceType().getTypeDeclaration().get();
						fqn = rrtd.getPackageName() + "." + rrtd.getClassName();
						if (!visited.contains(fqn)) {
							resolvedTypes.add(rrtd);
						}
						type = fqnify(fqn);
					} else if (rfd.getType().isTypeVariable()) {
						type = "bytes ";
					}
				}
				if (type != null) {
					sb.append("  ")
					.append(type)
					.append(" ")
					.append(rfd.getName())
					.append(" = ")
					.append(counter++)
					.append(";\n");
				}
			}

			// Add field for superclass.
			for (ResolvedReferenceType rrt : clazz.getAncestors()) {
				if (rrt.getTypeDeclaration().get() instanceof ReflectionClassDeclaration) {
					ReflectionClassDeclaration rcd = (ReflectionClassDeclaration) rrt.getTypeDeclaration().get();
					fqn = fqnify(rcd.getPackageName() + "." + rcd.getName());
					if (!visited.contains(fqn)) {
						resolvedTypes.add(rcd);
					}
					String superClassName = rcd.getName();
					String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1));
					sb.append("  ")
					.append(fqn)
					.append(" ")
					.append(superClassVariableName)
					.append(" = ")
					.append(counter++)
					.append(";\n");
					break;
				}
			}
			sb.append("}\n");
		}
	}

	static private String getEntityParameter(MethodDeclaration md) {
		for (Parameter p : md.getParameters()) {
			boolean isEntity = true;
			for (AnnotationExpr ae : p.getAnnotations()) {
				if (ANNOTATIONS.contains(ae.getNameAsString())) {
					isEntity = false;
					break;
				}
			}
			if (isEntity) {
				String rawType = p.getTypeAsString();
				String type = TYPE_MAP.get(rawType);
				if (type != null) {
					return PRIMITIVE_WRAPPER_TYPES.get(rawType);
				}
				// array?
				ResolvedType rt = p.getType().resolve();
				resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
				type = rt.describe();
				return fqnify(type);
			}
		}
		needEmpty = true;
		return "Empty";
	}

	static private String getReturnType(MethodDeclaration md) {
		for (Node node : md.getChildNodes()) {
			if (node instanceof Type) {
				if (node instanceof VoidType) {
					needEmpty = true;
					return "Empty";
				} else {
					String rawType = ((Type) node).asString();
					String type = TYPE_MAP.get(rawType);
					if (type != null) {
						return PRIMITIVE_WRAPPER_TYPES.get(rawType);
					}
					// array?
					ResolvedType rt = ((Type) node).resolve();
					resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
					type = ((Type) node).resolve().describe();
					return fqnify(type);
				}
			}
		}
		needEmpty = true;
		return "Empty";
	}

	static private boolean isResourceMethod(MethodDeclaration md) {
		for (AnnotationExpr ae : md.getAnnotations()) {
			if (HTTP_VERBS.contains(ae.getNameAsString().toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	static private String removeTypeVariables(String classType) {
		int left = classType.indexOf('<');
		if (left < 0) {
			return classType;
		}
		return classType.substring(0, left);
	}
	
	static private String fqnify(String s) {
		return s.replace(".", "_");
	}
}