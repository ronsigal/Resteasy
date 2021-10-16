package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
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
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

/**
 * Traverses a set of JAX-RS resources and creates a protobuf representation.
 * <p/>
 * <ol>
 *    <li>Find all JAX-RS resource methods and resource locators and create an rpc entry for each</li>
 *    <li>Find the transitive closure of the classes mentioned in the resource methods and locators
 *         and create a message entry for each.</li>
 * </ol>
 * <p/>
 * </pre>
 * For example,
 * <p/>
 * <pre>
 * public class CC1 {
 *
 *    &#064;Path("m1")
 *    &#064;GET
 *    String m1(CC2 cc2) {
 *       return "x";
 *    }
 *
 *    String m2(String s) {
 *       return "x";
 *    }
 *
 *    &#064;Path("m3")
 *    &#064;GET
 *    String m3(CC4 cc4) {
 *       return "x";
 *    }
 * }
 * </pre>
 * together with the class definitions
 * <p/>
 * <pre>
 * package io.grpc.classes;
 *
 * public class CC2 extends CC3 {
 *    int j;
 *
 *    public CC2(String s, int j) {
 *       super(s);
 *       this.j = j;
 *    }
 *
 *    public CC2() {}
 * }
 *
 * public class CC3 {
 *    String s;
 *
 *    public CC3(String s) {
 *       this.s = s;
 *    }
 *
 *    public CC3() {}
 * }
 *
 * package io.grpc.classes;
 *
 * public class CC4 {
 *    private String s;
 *    private CC5 cc5;
 *
 *    public CC4(String s, CC5 cc5) {
 *       this.s = s;
 *       this.cc5 = cc5;
 *    }
 *
 *    public CC4() {}
 * }
 *
 * package io.grpc.classes;
 *
 * public class CC5 {
 *    int k;
 *
 *    public CC5(int k) {
 *       this.k = k;
 *    }
 *
 *    public CC5() {}
 * }
 * </pre>
 * is translated to CC1.proto:
 * <p/>
 * <pre>
 * syntax = "proto3";
 * package io.grpc.classes;
 * option java_package = "io.grpc.classes";
 * option java_outer_classname = "CC1_proto";
 *
 * service CC1Service {
 *    rpc m1 (io_grpc_classes___CC2) returns (String);
 *    rpc m3 (io_grpc_classes___CC4) returns (String);
 * }
 *
 * message io_grpc_classes___CC2 {
 *    int32 j = 1;
 *    io_grpc_classes___CC3 cC3___super = 2;
 * }
 *
 * message io_grpc_classes___CC4 {
 *    string s = 3;
 *    io_grpc_classes___CC5 cc5 = 4;
 * }
 *
 * message io_grpc_classes___CC3 {
 *    string s = 5;
 * }
 *
 * message io_grpc_classes___CC5 {
 *    int32 k = 6;
 * }
 * </pre>
 * <p/>
 * <b>Notes.</b>
 * <ol>
 *    <li>{@code CC1.m2()} is not a resource method, so it does not appear in CC1.proto.
 *    <li>As of now, {@code JavaToProtobufGenerator} requires classes to have a no-arg constructor.
 *    <li>Protobuf syntax does not support inheritance, so {@code JavaToProtobufGenerator}
 *        treats a superclass as a special field. For example, {@code CC2}  is a subclass of {@code CC3},
 *        so each instance of {@code CC2} has a field named {@code cC3___super} of {@code type io_grpc_classes___CC3}.
 */
public class JavaToProtobufGenerator {

   private static Logger logger = Logger.getLogger(JavabufTranslatorGenerator.class);
   private static Map<String, String> TYPE_MAP = new HashMap<String, String>();
   private static Map<String, String> PRIMITIVE_WRAPPER_TYPES = new HashMap<String, String>();
   private static Map<String, String> PRIMITIVE_WRAPPER_DEFINITIONS = new HashMap<String, String>();
   private static Set<String> ANNOTATIONS = new HashSet<String>();
   private static Set<String> HTTP_VERBS = new HashSet<String>();
   private static boolean needEmpty = false;
   private static List<ResolvedReferenceTypeDeclaration> resolvedTypes = new CopyOnWriteArrayList<ResolvedReferenceTypeDeclaration>();
   private static Set<String> additionalClasses;// = new CopyOnWriteArraySet<String>();
   private static Set<String> visited = new HashSet<String>();
   private static JavaSymbolSolver symbolSolver;
   private static ClassVisitor classVisitor = new ClassVisitor();
   private static JaxrsResourceVisitor jaxrsResourceVisitor = new JaxrsResourceVisitor();
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
      TYPE_MAP.put("java.lang.String", "string");

      PRIMITIVE_WRAPPER_TYPES.put("short",   "Short");
      PRIMITIVE_WRAPPER_TYPES.put("int",     "Integer");
      PRIMITIVE_WRAPPER_TYPES.put("long",    "Long");
      PRIMITIVE_WRAPPER_TYPES.put("float",   "Float");
      PRIMITIVE_WRAPPER_TYPES.put("double",  "Double");
      PRIMITIVE_WRAPPER_TYPES.put("boolean", "Boolean");
      PRIMITIVE_WRAPPER_TYPES.put("char",    "Character");
      PRIMITIVE_WRAPPER_TYPES.put("string",  "String");

      PRIMITIVE_WRAPPER_DEFINITIONS.put("Short",     "message Short     {int32  value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Integer",   "message Integer   {int32  value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Long",      "message Long      {int64  value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Float",     "message Float     {float  value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Double",    "message Double    {double value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Boolean",   "message Boolean   {bool   value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("Character", "message Character {int32  value = $V$;}");
      PRIMITIVE_WRAPPER_DEFINITIONS.put("String",    "message String    {string value = $V$;}");

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
   }

   public static void main(String[] args) throws IOException
   {
      if (args.length != 5) {
         logger.info("need five args");
         logger.info("  arg[0]: root directory");
         logger.info("  arg[1]: package to be used in .proto file");
         logger.info("  arg[2]: java package to be used in .proto file");
         logger.info("  arg[3]: java outer classname to be generated from .proto file");
         logger.info("  arg[4]: comma separated of addition classes");
         return;
      }
      additionalClasses = "empty".equals(args[4]) ? new CopyOnWriteArraySet<String>()
                                                  : new CopyOnWriteArraySet<String>(Arrays.asList(args[4].split(",")));
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
      createProtobufDirectory(args);
   }

   private static void protobufHeader(String[] args, StringBuilder sb)
   {
      sb.append("syntax = \"proto3\";\n");
      sb.append("package " + args[1].replace('-', '.') + ";\n");
      sb.append("option java_package = \"" + args[2] + "\";\n");
      sb.append("option java_outer_classname = \"" + args[3] + "_proto\";\n");
   }

   /**
    * Visit all JAX-RS resource classes discovered in project's src/main/java
    */
   private void processClasses(String[] args, StringBuilder sb) throws IOException {
      Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

      // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
      Path path = Path.of(args[0], "/src/main/java/");
      SourceRoot sourceRoot = new SourceRoot(path);
      TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
      TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(path);
      CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
      combinedTypeSolver.add(reflectionTypeSolver);
      combinedTypeSolver.add(javaParserTypeSolver);
      symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
      sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
      List<ParseResult<CompilationUnit>> list = sourceRoot.tryToParseParallelized();
      for (ParseResult<CompilationUnit> p : list) {
         jaxrsResourceVisitor.visit(p.getResult().get(), sb);
      }
      processAdditionalClasses(symbolSolver, sb);
   }

   /****************************************************************************/
   /****************************** primary methods *****************************
   /
    * @throws FileNotFoundException ****************************************************************************/

   private static void processAdditionalClasses(JavaSymbolSolver symbolSolver, StringBuilder sb) throws FileNotFoundException {
      StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
      while (!additionalClasses.isEmpty()) {
         for (String filename : additionalClasses) {
            System.out.println("processAdditionalClasses(): additionalClasses.size(): " + additionalClasses.size());
            System.out.println("filename: " + filename);
            int n = filename.indexOf(":");
            if (n < 0) {
               throw new RuntimeException("bad syntax: " + filename);
            }
            String dir = filename.substring(0, n).trim();
            filename = dir + "/" + filename.substring(n + 1).replace(".", "/") + ".java";
            CompilationUnit cu = StaticJavaParser.parse(new File(filename));
            AdditionalClassVisitor additionalClassVisitor = new AdditionalClassVisitor(dir);
            additionalClassVisitor.visit(cu, sb);
         }
      }
   }

   private static void finishProto(StringBuilder sb) {
      if (needEmpty) {
         sb.append("\nmessage Empty {}");
      }

      for (String wrapper : PRIMITIVE_WRAPPER_DEFINITIONS.values()) {
         sb.append("\n").append(wrapper.replace("$V$", String.valueOf(counter++)));
      }
   }

   private static void writeProtoFile(String[] args, StringBuilder sb) throws IOException {
      String path = args[0];
      String generatedSources = "src/main/proto";
      for (String s : generatedSources.split("/")) {
         path += "/" + s;
         File dir = new File(path);
         if(!dir.exists()){
            dir.mkdir();
         }
      }
      File file = new File(path + "/" + args[3] + ".proto");
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
   }

   private static void createProtobufDirectory(String[] args) {
      String path = args[0] + "/target/generatedSources";
      for (String s : args[1].split("\\.")) {
         path += "/" + s;
         File dir = new File(path);
         if(!dir.exists()) {
            dir.mkdir();
         }
      }
   }

   /****************************************************************************/
   /******************************** classes ***********************************
   /****************************************************************************/

   /**
    * Visits each class in the transitive closure of all classes referenced in the
    * signatures of resource methods. Creates a service with an rpc declaration for
    * each resource method or locator.
    */
   static class JaxrsResourceVisitor extends VoidVisitorAdapter<StringBuilder> {

      public void visit(final ClassOrInterfaceDeclaration subClass, StringBuilder sb) {
         boolean started = false;
         Optional<AnnotationExpr> opt = subClass.getAnnotationByName("Path");
         SingleMemberAnnotationExpr annotationExpr = opt.isPresent() ? (SingleMemberAnnotationExpr) opt.get() : null;
         String classPath = "";
         if (annotationExpr != null) {
            classPath = annotationExpr.getMemberValue().toString();
            classPath = classPath.substring(1, classPath.length() - 1);
         }
         for (BodyDeclaration<?> bd : subClass.getMembers()) {
            if (bd instanceof MethodDeclaration) {
               MethodDeclaration md = (MethodDeclaration) bd;
               if (!isResourceMethod(md)) {
                  continue;
               }
               String methodPath = "";
               opt = md.getAnnotationByName("Path");
               annotationExpr = opt.isPresent() ? (SingleMemberAnnotationExpr) opt.get() : null;
               if (annotationExpr != null) {
                  methodPath = annotationExpr.getMemberValue().toString();
                  methodPath = methodPath.substring(1, methodPath.length() - 1);  
               }
               String httpMethod = getHttpMethod(md);
               // Add service with a method for each resource method in class.
               if (!started) {
                  sb.append("\nservice ")
                  .append(fqnify(subClass.getNameAsString()))
                  .append("Service {\n");
                  started = true;
               }
               sb.append("// ").append(classPath).append("/").append(methodPath).append(" ").append(httpMethod).append("\n");
               sb.append("  rpc ")
               .append(md.getNameAsString())
               .append(" (")
               .append(getEntityParameter(md))
               .append(") returns (")
               .append(getReturnType(md))
               .append(");\n");

               // Add each parameter and return type to resolvedTypes for further processing.
               for (Parameter p : md.getParameters()) {
                  if (p.getType().resolve().isPrimitive()) {
                     continue;
                  }
                  ReferenceTypeImpl rt = (ReferenceTypeImpl) p.getType().resolve();
                  ResolvedReferenceTypeDeclaration rrtd = rt.getTypeDeclaration().get();
                  String type = rt.asReferenceType().getQualifiedName();
                  if (!visited.contains(type)) {
                     resolvedTypes.add(rrtd);
                  }
               }
            }
         }
         if (started) {
            sb.append("}\n");
         }
      }
   }

   /**
    * Visit all classes discovered by JaxrsResourceVisitor in the process of visiting all JAX-RS resources
    */
   static class ClassVisitor extends VoidVisitorAdapter<StringBuilder> {

      /**
       * For each class, create a message type with a field for each variable in the class.
       */
      public void visit(ResolvedReferenceTypeDeclaration clazz, StringBuilder sb) {
         resolvedTypes.remove(clazz);
         if (PRIMITIVE_WRAPPER_DEFINITIONS.containsKey(clazz.getClassName())) {
            return;
         }
         if (Response.class.getName().equals(clazz.getQualifiedName())) {
            return;
         }
         String fqn = clazz.getQualifiedName();
         if (visited.contains(fqn)) {
            return;
         }
         visited.add(fqn);

         //         if (subClass.isInterface()) {
         //            return;
         //         }

         // Begin protobuf message definition.
         sb.append("\nmessage ").append(fqnifyClass(fqn)).append(" {\n");

         // Scan all variables in class.
         for (ResolvedFieldDeclaration rfd: clazz.getDeclaredFields()) {
            String type = null;
            if (rfd.getType().isPrimitive()|| rfd.getType().isReferenceType() && String.class.getName().equals(rfd.getType().asReferenceType().getQualifiedName())) {
               type = TYPE_MAP.get(rfd.getType().describe());
            }  else if (rfd.getType() instanceof ResolvedArrayType) {
               ResolvedArrayType rat = (ResolvedArrayType) rfd.getType();
               ResolvedType ct = rat.getComponentType();
               if (ct.isPrimitive()) {
                  type = "repeated " + TYPE_MAP.get(removeTypeVariables(ct.describe()));
               } else {
                  fqn = removeTypeVariables(ct.describe());
                  if (!ct.isReferenceType()) {
                     continue;
                  }
                  if (!visited.contains(fqn)) {
                     resolvedTypes.add(ct.asReferenceType().getTypeDeclaration().get());
                  }
                  type = "repeated " + fqnifyClass(fqn);
               }
            } else { // Defined type
               if (rfd.getType().isReferenceType()) {
                  ResolvedReferenceTypeDeclaration rrtd = (ResolvedReferenceTypeDeclaration) rfd.getType().asReferenceType().getTypeDeclaration().get();
                  fqn = rrtd.getPackageName() + "." + rrtd.getClassName();
                  if (!visited.contains(fqn)) {
                     resolvedTypes.add(rrtd);
                  }
                  type = fqnifyClass(fqn);
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
               if (Object.class.getName().equals(rcd.getQualifiedName())) {
                  continue;
               }
               fqn = fqnifyClass(rcd.getPackageName() + "." + rcd.getName());
               if (!visited.contains(fqn)) {
                  resolvedTypes.add(rcd);
               }
               String superClassName = rcd.getName();
               String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1)) + "___super";
               sb.append("  ")
               .append(fqn)
               .append(" ")
               .append(superClassVariableName)
               .append(" = ")
               .append(counter++)
               .append(";\n");
               break;
            } else if (rrt.getTypeDeclaration().get() instanceof JavaParserClassDeclaration) {
               JavaParserClassDeclaration jpcd = (JavaParserClassDeclaration) rrt.getTypeDeclaration().get();
               ResolvedClassDeclaration rcd = jpcd.asClass();
               if (Object.class.getName().equals(rcd.getClassName())) {
                  continue;
               }
               fqn = rcd.getPackageName() + "." + rcd.getName();
               if (!visited.contains(fqn)) {
                  resolvedTypes.add(rcd);
               }
               fqn = fqnifyClass(fqn);
               String superClassName = rcd.getName();
               String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1)) + "___super";
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

   /**
    * Visit all classes discovered by JaxrsResourceVisitor in the process of visiting all JAX-RS resources
    */
   static class AdditionalClassVisitor extends VoidVisitorAdapter<StringBuilder> {
      private String dir;

      public AdditionalClassVisitor(String dir) {
         this.dir = dir;
      }

      /**
       * For each class, create a message type with a field for each variable in the class.
       */
      public void visit(ClassOrInterfaceDeclaration clazz, StringBuilder sb) {
         if (PRIMITIVE_WRAPPER_DEFINITIONS.containsKey(clazz.getName().asString())) {
            return;
         }
         String packageName = getPackageName(clazz);
         String fqn = packageName + "." + clazz.getNameAsString();
         String filename = dir + ":" + fqn;
         additionalClasses.remove(filename);
         
         if (visited.contains(fqn)) {
            return;
         }
         visited.add(fqn);

         //         if (subClass.isInterface()) {
         //            return;
         //         }

         // Begin protobuf message definition.
         sb.append("\nmessage ").append(fqnifyClass(fqn)).append(" {\n");

         // Scan all variables in class.
         for (FieldDeclaration fd: clazz.getFields()) {
            ResolvedFieldDeclaration rfd = fd.resolve();
            ResolvedType type = rfd.getType();
            String typeName = type.describe();
            if (TYPE_MAP.containsKey(typeName)) {
               typeName = TYPE_MAP.get(typeName);
            } else if (type.isArray()) {
               ResolvedType ct = type.asArrayType().getComponentType();
               if (ct.isPrimitive()) {
                  typeName = "repeated " + typeName;
               } else {
                  fqn = type.describe();
                     additionalClasses.add(dir + ":" + fqn);
                  typeName = "repeated " + fqnifyClass(fqn);
               }
            } else { // Defined type
               fqn = type.describe();
               additionalClasses.add(dir + ":" + fqn);
               typeName = fqnifyClass(type.describe());
               System.out.println("visit(2): additionalClasses.size(): " + additionalClasses.size());
               //               } else if (fd.getType().isTypeVariable()) {
               //                  type = "bytes ";
               //               }
            }
            if (type != null) {
               sb.append("  ")
               .append(typeName)
               .append(" ")
               .append(rfd.getName())
               .append(" = ")
               .append(counter++)
               .append(";\n");
            }
         }

         // Add field for superclass.
         for (ResolvedReferenceType rrt : clazz.resolve().getAllAncestors()) {
            if (Object.class.getName().equals(rrt.getQualifiedName())) {
               continue;
            }
//            if (rrt.getTypeDeclaration().get() instanceof ReflectionClassDeclaration) {
//               ReflectionClassDeclaration rcd = (ReflectionClassDeclaration) rrt.getTypeDeclaration().get();
//               if (Object.class.getName().equals(rcd.getQualifiedName())) {
//                  continue;
//               }
//               fqn = fqnify(rcd.getPackageName() + "." + rcd.getName());
//               if (!visited.contains(fqn)) {
//                  resolvedTypes.add(rcd);
//               }
//               String superClassName = rcd.getName();
//               String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1)) + "___super";
//               sb.append("  ")
//               .append(fqn)
//               .append(" ")
//               .append(superClassVariableName)
//               .append(" = ")
//               .append(counter++)
//               .append(";\n");
//               break;
//            } else 
            if (rrt.getTypeDeclaration().get() instanceof JavaParserClassDeclaration) {
               JavaParserClassDeclaration jpcd = (JavaParserClassDeclaration) rrt.getTypeDeclaration().get();
               ResolvedClassDeclaration rcd = jpcd.asClass();
               if (Object.class.getName().equals(rcd.getClassName())) {
                  continue;
               }
               fqn = rcd.getPackageName() + "." + rcd.getName();
               if (!visited.contains(fqn)) { // should fqn be fqnifyed?
                  additionalClasses.add(dir + ":" + fqn);   // add to additionalClasses
               }
               fqn = fqnifyClass(fqn);
               String superClassName = rcd.getName();
               String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0))).concat(superClassName.substring(1)) + "___super";
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

   static private String getPackageName(ClassOrInterfaceDeclaration clazz) {
      String fqn = clazz.getFullyQualifiedName().orElse(null);
      if (fqn == null) {
         return null;
      }
      int index = fqn.lastIndexOf(".");
      return fqn.substring(0, index);
   }

   /****************************************************************************/
   /****************************** utility methods *****************************
   /****************************************************************************/
   private static String getEntityParameter(MethodDeclaration md) {
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
            String type = TYPE_MAP.get(rawType.toLowerCase());
            if (type != null) {
               return PRIMITIVE_WRAPPER_TYPES.get(rawType.toLowerCase());
            }
            if (PRIMITIVE_WRAPPER_TYPES.containsValue(rawType)) {
               return rawType;
            }
            // array?
            ResolvedType rt = p.getType().resolve();
            resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
            type = rt.describe();
            int n = type.lastIndexOf(".");
            return fqnify(type.substring(0, n)) + "___" + type.substring(n + 1);
         }
      }
      needEmpty = true;
      return "Empty";
   }

   private static String getReturnType(MethodDeclaration md) {
      for (Node node : md.getChildNodes()) {
         if (node instanceof Type) {
            if (node instanceof VoidType) {
               needEmpty = true;
               return "Empty";
            } else {
               String rawType = ((Type) node).asString();
               System.out.println("return type: rawType: " + rawType);
               String type = TYPE_MAP.get(rawType.toLowerCase());
               System.out.println("return type: type: " + type);
               if (type != null) {
                  return PRIMITIVE_WRAPPER_TYPES.get(rawType.toLowerCase());
               }
               if (PRIMITIVE_WRAPPER_TYPES.containsValue(rawType)) {
                  return rawType;
               }
//               if ("String".equals(rawType)) {
//                  return "String";
//               }
               // array?
               ResolvedType rt = ((Type) node).resolve();
               resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
               type = ((Type) node).resolve().describe();
               return fqnifyClass(type);
            }
         }
      }
      needEmpty = true;
      return "Empty";
   }

   // @Path() ???
   private static boolean isResourceMethod(MethodDeclaration md) {
      for (AnnotationExpr ae : md.getAnnotations()) {
         if (HTTP_VERBS.contains(ae.getNameAsString().toUpperCase())) {
            return true;
         }
      }
      return false;
   }

   private static String removeTypeVariables(String classType) {
      int left = classType.indexOf('<');
      if (left < 0) {
         return classType;
      }
      return classType.substring(0, left);
   }

   private static String fqnify(String s) {
      return s.replace(".", "_");
   }

   private static String fqnifyClass(String s) {
      String t = s.replace(".", "_");
      int i = t.lastIndexOf("_");
      return t.substring(0, i) + "__" + t.substring(i);
   }
   
   private static String getHttpMethod(MethodDeclaration md) {
      if (!md.getAnnotationByName("DELETE").isEmpty()) {
         return "DELETE";
      }
      if (!md.getAnnotationByName("GET").isEmpty()) {
         return "GET";
      }
      if (!md.getAnnotationByName("HEAD").isEmpty()) {
         return "HEAD";
      }
      if (!md.getAnnotationByName("POST").isEmpty()) {
         return "POST";
      }
      if (!md.getAnnotationByName("PUT").isEmpty()) {
         return "PUT";
      }
      return "";
   }
}