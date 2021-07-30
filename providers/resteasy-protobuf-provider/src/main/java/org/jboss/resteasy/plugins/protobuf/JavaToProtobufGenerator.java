package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
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
      if (args.length != 5) {
         System.out.println("need five args");
         System.out.println("  arg[0]: root directory");
         System.out.println("  arg[1]: java file");
         System.out.println("  arg[2]: package to be used in .proto file");
         System.out.println("  arg[3]: java package to be used in .proto file");
         System.out.println("  arg[4]: java outer classname to be generated from .proto file");
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
      createProtobufDirectory(args);
   }

   private static void protobufHeader(String[] args, StringBuilder sb)
   {
      sb.append("syntax = \"proto3\";\n");
      sb.append("package " + args[2] + ";\n");
      sb.append("option java_package = \"" + args[3] + "\";\n");
      sb.append("option java_outer_classname = \"" + args[4] + "_proto\";\n");
   }

   /**
    * Visit all JAX-RS resource classes discovered in project's src/main/java
    */
   private void processClasses(String[] args, StringBuilder sb) throws IOException {
      Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

      // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
      Path path = Path.of(args[0], "/src/main/java/");
      // In this case the root directory is found by taking the root from the current Maven module,
      // with src/main/resources appended.
      //      SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(JavaToProtobufGenerator2.class).resolve("src/main/java/" + dirify(args[2])));
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
   }

   /****************************************************************************/
   /****************************** primary methods *****************************
   /****************************************************************************/
   private static void finishProto(StringBuilder sb) {
      if (needEmpty) {
         sb.append("\nmessage Empty {}");
      }
      
      for (String wrapper : PRIMITIVE_WRAPPERS.values()) {
         sb.append("\n").append(wrapper.replace("$V$", String.valueOf(counter++)));
      }
   }

   static private void writeProtoFile(String[] args, StringBuilder sb) throws IOException {
      String path = args[0];
      String generatedSources = "target/generatedSources/protobuf/idl";
      for (String s : generatedSources.split("/")) {
         path += "/" + s;
         File dir = new File(path);
         System.out.println("path: " + path + ": " + dir.exists());
         if(!dir.exists()){
            dir.mkdir();
         } 
      }
      File file = new File(path + "/" + args[4] + ".proto");
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
   }

   static private void createProtobufDirectory(String[] args) {
      String path = args[0] + "/target/generatedSources";
      for (String s : args[2].split("\\.")) {
           path += "/" + s;
            File dir = new File(path);
            if(!dir.exists()){
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
         for (BodyDeclaration<?> bd : subClass.getMembers()) {
            if (bd instanceof MethodDeclaration) {
               MethodDeclaration md = (MethodDeclaration) bd;
               if (!isResourceMethod(md)) {
                  continue;
               }
               // Add service with a method for each resource method in class.
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

               // Add each parameter and return type to resolvedTypes for further processing.
               for (Parameter p : md.getParameters()) {
                  if (p.getType().resolve().isPrimitive()) {
                     continue;
                  }
                  ReferenceTypeImpl rt = (ReferenceTypeImpl) p.getType().resolve();
                  ResolvedReferenceTypeDeclaration rrtd = rt.getTypeDeclaration().get();
                  String type = fqnify(removeTypeVariables(rt.asReferenceType().getQualifiedName()));
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
         if (PRIMITIVE_WRAPPERS.containsKey(clazz.getClassName())) {
            return;
         }
         String fqn = clazz.getPackageName() + "___" + clazz.getClassName();
         if (visited.contains(fqn)) {
            return;
         }
         visited.add(fqn);

         //         if (subClass.isInterface()) {
         //            return;
         //         }
         
         // Begin protobuf message definition.
         sb.append("\nmessage ").append(fqnify(fqn)).append(" {\n");

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
                  type = "repeated " + fqnify(fqn);
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
               fqn = fqnify(rcd.getPackageName() + "." + rcd.getName());
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
               
            }
         }
         sb.append("}\n");
      }
   }


   /****************************************************************************/
   /****************************** utility methods *****************************
   /****************************************************************************/
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
            int n = type.lastIndexOf(".");
            return fqnify(type.substring(0, n)) + "___" + type.substring(n + 1);
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
                  return PRIMITIVE_WRAPPER_TYPES.get(rawType.toLowerCase());
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

   // @Path() ???
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

   static private String fqnifyClass(String s) {
      String t = s.replace(".", "_");
      int i = t.lastIndexOf("_");
      return t.substring(0, i) + "__" + t.substring(i);
   }
}