package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

import io.grpc.examples.classes2.C1;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

/**
 * Some code that uses JavaParser.
 */
public class JavabufTranslatorGenerator {

   public interface AssignTo
   {
      public void assign(Object from, DynamicMessage.Builder builder);
   }

   public interface AssignFrom
   {
      public void assign(Message message, Object object);
   }

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

   public static void main(String[] args)
   {
      if (args.length != 2) {
         System.out.println("need two args:");
         System.out.println("  arg[0]: root directory");
         System.out.println("  arg[1]: javabuf wrapper class name");
         return;
      }
      try 
      {
//         System.out.println(io.grpc.examples.classes.C_proto.class.getClassLoader());
//         System.out.println(Thread.currentThread().getContextClassLoader());
//         System.out.println(JavabufTranslatorGenerator.class.getClassLoader());
         System.out.println("args[0]: " + args[0]);
         System.out.println("args[1]: " + args[1]);
         int index = args[1].lastIndexOf('.');
         String simpleName = index < 0 ? args[1] : args[1].substring(index + 1);
         String translatorClass = simpleName + "_JavabufTranslator";
         Class<?> wrapperClass = Class.forName(args[1] + "_proto", true, Thread.currentThread().getContextClassLoader());
         StringBuilder sb = new StringBuilder();
         classHeader(args, translatorClass, wrapperClass, sb);
         classBody(translatorClass, wrapperClass, sb);
         //      new JavabufTranslatorGenerator().processClasses(args, sb);
         //      while (!resolvedTypes.isEmpty()) {
         //         for (ResolvedReferenceTypeDeclaration rrtd : resolvedTypes) {
         //            classVisitor.visit(rrtd, sb);
         //         }
         //      }
         finishClass(sb);
         writeTranslatorClass(args, translatorClass, sb);
      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static void classHeader(String[] args, String translatorClass, Class<?> wrapperClass, StringBuilder sb)
   {
      sb.append("package ").append(wrapperClass.getPackage().getName()).append(";\n\n");
      imports(wrapperClass, sb);
      sb.append(   "public class ")
       .append(translatorClass)
       .append(" {\n");
   }
   
   private static void imports(Class<?> wrapperClass, StringBuilder sb)
   {
      sb.append("import java.lang.reflect.Field;\n")
        .append("import java.util.ArrayList;\n")
        .append("import java.util.HashMap;\n")
        .append("import java.util.List;\n")
        .append("import java.util.Map;\n")
        .append("import com.google.protobuf.Descriptors.Descriptor;\n")
        .append("import com.google.protobuf.Descriptors.FieldDescriptor;\n")
        .append("import com.google.protobuf.DynamicMessage;\n")
        .append("import com.google.protobuf.Message;\n")
        .append("import ").append(AssignFromJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(AssignToJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(TranslateToJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(wrapperClass.getCanonicalName()).append(";\n");
      for (Class<?> clazz : wrapperClass.getClasses())
      {
         if (clazz.isInterface())
         {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if (PRIMITIVE_WRAPPER_TYPES.containsKey(simpleName) || PRIMITIVE_WRAPPERS.containsKey(simpleName)) {
            continue;
         }
         sb.append("import ").append(clazz.getCanonicalName()).append(";\n");
         String packageName = dotify(simpleName.substring(0, simpleName.indexOf("___")));
         sb.append("import ").append(packageName).append(".").append(originalSimpleName(clazz.getSimpleName())).append(";\n");
      }
      sb.append("\n");
   }

   private static void classBody(String translatorClass, Class<?> wrapperClass, StringBuilder sb) throws Exception
   {
      Class<?>[] classes = wrapperClass.getClasses();
      privateVariables(sb);
      staticInit(translatorClass, wrapperClass, classes, sb);
      publicMethod(sb);
      privateMethods(sb);
      for (Class<?> clazz: classes)
      {
         if (clazz.isInterface()) {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if (PRIMITIVE_WRAPPER_TYPES.containsKey(simpleName) || PRIMITIVE_WRAPPERS.containsKey(simpleName)) {
            continue;
         }
         createTranslator(clazz, sb);
      }
   }
   /*
      toJavabufMap.put(C1.class, toJavabuf(C1.class, new io_grpc_examples_classes_C1_ToJavabuf()));
   toJavabufMap.put(C3.class, toJavabuf(C3.class, new io_grpc_examples_classes_C3_ToJavabuf()));
    */

   private static void staticInit(String translatorClass, Class<?> wrapperClass, Class<?>[] classes, StringBuilder sb) {
//      sb.append("   public ")
//        .append(translatorClass)
//        .append("() {\n");
      sb.append("   static {\n");
      for (Class<?> clazz: classes) {
         if (clazz.isInterface()) {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if (PRIMITIVE_WRAPPER_TYPES.containsKey(simpleName) || PRIMITIVE_WRAPPERS.containsKey(simpleName)) {
            continue;
         }
         int i = simpleName.lastIndexOf("___");
         String originalClassName = i < 0 ? simpleName : simpleName.substring(i + 3);
         sb.append("      toJavabufMap.put(")
           .append(originalClassName)
           .append(".class, new ")
           .append(simpleName)
           .append("_ToJavabuf());\n");
      }
      sb.append("   }\n\n");
   }
   
   /*
   public static Message translateToJavabuf(Object o) {
      TranslateToJavabuf ttj = toJavabufMap.get(o.getClass());
      if (ttj == null) {
         throw new RuntimeException(o.getClass() + " is not recognized");
      }
      return ttj.assignToJavabuf(o);
   }
    */
   private static void publicMethod(StringBuilder sb) {
      sb.append("\n")
         .append("   public static Message translateToJavabuf(Object o) {\n")
         .append("      TranslateToJavabuf ttj = toJavabufMap.get(o.getClass());\n"
              + "       if (ttj == null) {\n"
              + "          throw new RuntimeException(o.getClass() + \" is not recognized\");\n"
              + "       }\n"
              + "       return ttj.assignToJavabuf(o);\n"
              + "    }\n");
   }
   
   private static void createTranslator(Class<?> clazz, StringBuilder sb) throws Exception
   {
      createTranslatorToJavabuf(clazz, sb);
      createTranslatorFromJavabuf(clazz, sb);
   }
   
   private static void privateVariables(StringBuilder sb) {
      sb.append("   private static Map<Class<?>, TranslateToJavabuf> toJavabufMap = new HashMap<Class<?>, TranslateToJavabuf>();\n\n");
   }

   private static void privateMethods(StringBuilder sb) {
      sb.append(
            "   private static AssignToJavabuf toJavabuf(Class<?> javaClass, FieldDescriptor fd) {\n" +
            "      try {\n" +
            "         AssignToJavabuf assignToJavabuf = (obj, messageBuilder) -> {\n" +
            "            try {\n" +
            "               if (isSuperClass(fd.getName())) {\n" +
            "                  Message message = toJavabufMap.get(obj.getClass().getSuperclass()).assignToJavabuf(obj);\n" +
            "                  messageBuilder.setField(fd, message);\n" +
            "               } else {\n" +
            "                  final Field field = javaClass.getDeclaredField(fd.getName());\n" +
            "                  field.setAccessible(true);\n" +
            "                  if (toJavabufMap.keySet().contains(field.getType())) {\n" +
            "                     Message message = toJavabufMap.get(field.getType()).assignToJavabuf(field.get(obj));\n" +
            "                     messageBuilder.setField(fd, message);\n" +
            "                  } else {\n" +
            "                     messageBuilder.setField(fd, field.get(obj));\n" +
            "                  }\n" +
            "               }\n" +
            "            } catch (Exception e) {\n" +
            "               //\n" +
            "            }\n" +
            "         };\n" +
            "         return assignToJavabuf;\n" +
            "      } catch (Exception e) {\n" +
            "         throw new RuntimeException(e);\n" +
            "      }\n" +
            "   }\n\n"
      );
      sb.append(
           "   private static boolean isSuperClass(String fieldName) {\n" +
           "      return fieldName.endsWith(\"___super\");\n" +
           "   }\n\n"
      );
   }
   private static void createTranslatorToJavabuf(Class<?> clazz, StringBuilder sb) throws Exception
   {
      sb.append("   static class ")
        .append(fqnify(clazz.getSimpleName())).append("_ToJavabuf implements TranslateToJavabuf {\n")
        .append("      private static Descriptor descriptor = ").append(clazz.getCanonicalName()).append(".getDescriptor();\n")
//        .append("      private static List<FieldDescriptor> fieldDescriptors = descriptor.getFields();\n")
        .append("      private static DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);\n")
        .append("      private static List<AssignToJavabuf> assignList = new ArrayList<AssignToJavabuf>();\n\n")
        .append("      static {\n")
//        .append("         for (final FieldDescriptor fd : fieldDescriptors) {\n")
//        .append("            assignList.add(toJavabuf(").append(originalSimpleName(clazz.getSimpleName())).append(".class, fd));\n")
//        .append("         }\n")
//        .append("         for (Field f : ").append(clazz.getSimpleName()).append(".class.getDeclaredFields()) {\n")
//        .append("            assignList.add(toJavabuf(f.getType(), descriptor.findFieldByName(f.getName())));\n")
        .append("         for (FieldDescriptor f : descriptor.getFields()) {\n")
        .append("            String name = f.getName();\n")
        .append("            if (name.endsWith(\"_\")) {\n")
        .append("               name = name.substring(0, name.length() - 1);\n")
        .append("            }\n")
        .append("            if (descriptor.findFieldByName(name) == null) {\n")
        .append("               continue;\n")
        .append("            }\n")
        .append("            assignList.add(toJavabuf(").append(originalSimpleName(clazz.getSimpleName())).append(".class, descriptor.findFieldByName(name)));\n")
        .append("         }\n")
        .append("      }\n\n")
        .append("      public Message assignToJavabuf(Object c1) {\n")
        .append("         for (AssignToJavabuf assignTo : assignList) {\n")
        .append("            try {\n")
        .append("               assignTo.assign(c1, builder);\n")
        .append("            } catch (Exception e) {\n")
        .append("               throw new RuntimeException(e);\n")
        .append("            }\n")
        .append("         }\n")
        .append("         return builder.build();\n")
        .append("      }\n")
        .append("   }\n\n");
      }
   /*
             String name = f.getName();
            if (name.endsWith("_")) {
               name = name.substring(0, name.length() - 1);
            }
            if (descriptor.findFieldByName(name) == null) {
               continue;
            }
            assignList.add(toJavabuf(C1.class, descriptor.findFieldByName(name)));
    */
//      if (true) return;
//      Method getDefaultInstance = clazz.getMethod("getDefaultInstance");
//      Message message = (Message) getDefaultInstance.invoke(null);
//      Descriptor descriptor = message.getDescriptorForType();
//      List<FieldDescriptor> list = descriptor.getFields();
//      final DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
//      List<AssignTo> assignList = new ArrayList<AssignTo>();
//      for (final FieldDescriptor fd : list)
//      {
//         final Field field = clazz.getDeclaredField(fd.getName());
//         field.setAccessible(true);
//         AssignTo assign = (obj, messageBuilder) ->
//         {
//            try
//            {
//               messageBuilder.setField(fd, field.get(obj));
//            }
//            catch (Exception e)
//            {
//               //
//            }
//         };
//         assignList.add(assign);
//         assign.assign(clazz, builder);
//      }
//   }

   private static void createTranslatorFromJavabuf(Class<?> clazz, StringBuilder sb)
   {

   }

   private static void finishClass(StringBuilder sb) {
      sb.append("}\n");
   }

   private void processClasses(String[] args, StringBuilder sb) throws IOException {
      Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

      // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
      // In this case the root directory is found by taking the root from the current Maven module,
      // with src/main/resources appended.
      SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(JavabufTranslatorGenerator.class).resolve("src/main/java"));
      TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
      TypeSolver javaParserTypeSolver = new JavaParserTypeSolver("src/main/java");
      CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
      combinedTypeSolver.add(reflectionTypeSolver);
      combinedTypeSolver.add(javaParserTypeSolver);
      symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
      sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
      //      CompilationUnit cu = sourceRoot.parse(dirify(args[1]), args[0]);
      sourceRoot.tryToParseParallelized();
      //      classVisitor.visit(cu, sb);
   }

   static private void writeTranslatorClass(String[] args, String translatorClass, StringBuilder sb) throws IOException {
      StringBuilder root = new StringBuilder("target/generatedSources");
      File dir = new File(root.toString());
      if(!dir.exists())
      {
         dir.mkdir();
      }
      String pkg = args[1].lastIndexOf(".") < 0 ? "" : args[1].substring(0, args[1].lastIndexOf("."));
      String simpleName = args[1].lastIndexOf(".") < 0 ? args[1] : args[1].substring(args[1].lastIndexOf(".") + 1);
      for (String s : pkg.split("\\."))
      {
         dir = new File(root.append("/").append(s).toString());
         if (!dir.exists())
         {
            dir.mkdir();
         }
      }
      File file = new File(root.append("/").append(translatorClass).append(".java").toString());
      if (!file.exists()) {
         file.createNewFile();
      }
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
         //         if (subClass.isInterface()) {
         //            return;
         //         }
         ResolvedReferenceTypeDeclaration rrtd = subClass.resolve();
         String fqn = rrtd.getPackageName() + "." + rrtd.getClassName();
         if (visited.contains(fqn)) {
            return;
         }
         visited.add(fqn);
         System.out.println("class: " + subClass.getNameAsString());

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

         //         if (subClass.isInterface()) {
         //            return;
         //         }

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

   static private String dotify(String s) {
      return s.replace("_", ".");
   }
   
   static private String dirify(String s) {
      return s.replace(".", "/");
   }
   
   static private String originalSimpleName(String s) {
      int i = s.lastIndexOf("___");
      return i < 0 ? s : s.substring(i + 3);
   }

   private static Method getGetMethod(Class<?> clazz, String fieldName) throws NoSuchMethodException
   {
      StringBuilder sb = new StringBuilder("get");
      sb.append(Character.toUpperCase(fieldName.charAt(0)));
      if (fieldName.length() > 1)
      {
         sb.append(fieldName.substring(1));
      }
      return clazz.getMethod(sb.toString());
   }
}