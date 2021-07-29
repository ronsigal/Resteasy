package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

   private static Map<String, String> PRIMITIVE_WRAPPERS = new HashMap<String, String>();
   private static Map<String, String> PRIMITIVE_WRAPPER_TYPES = new HashMap<String, String>();
   
   static {
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
         int index = args[1].lastIndexOf('.');
         String simpleName = index < 0 ? args[1] : args[1].substring(index + 1);
         String translatorClass = simpleName + "_JavabufTranslator";
         Class<?> wrapperClass = Class.forName(args[1] + "_proto", true, Thread.currentThread().getContextClassLoader());
         StringBuilder sb = new StringBuilder();
         classHeader(args, translatorClass, wrapperClass, sb);
         classBody(translatorClass, wrapperClass, sb);
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
        .append("import ").append(TranslateFromJavabuf.class.getCanonicalName()).append(";\n")
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
      publicMethods(sb);
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

   private static void staticInit(String translatorClass, Class<?> wrapperClass, Class<?>[] classes, StringBuilder sb) {
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
         sb.append("      fromJavabufMap.put(")
           .append("\"" + simpleName + "\"")
           .append(", new ")
           .append(simpleName)
           .append("_FromJavabuf());\n");
      }
      sb.append("   }\n\n");
   }

   private static void publicMethods(StringBuilder sb) {
      sb.append("   public static Message translateToJavabuf(Object o) {\n")
        .append("      TranslateToJavabuf ttj = toJavabufMap.get(o.getClass());\n")
        .append("      if (ttj == null) {\n")
        .append("         throw new RuntimeException(o.getClass() + \" is not recognized\");\n")
        .append("      }\n")
        .append("      return ttj.assignToJavabuf(o);\n")
        .append("   }\n\n")
        .append("   public static Object translateFromJavabuf(Message message) {\n")
        .append("      String s = null;\n")
        .append("      try {\n")
        .append("         s = message.getDescriptorForType().getFullName();\n")
        .append("         s = s.substring(s.lastIndexOf(\".\") + 1);\n")
        .append("         TranslateFromJavabuf tfj = fromJavabufMap.get(s);\n")
        .append("         if (tfj == null) {\n")
        .append("            throw new RuntimeException(message.getClass() + \" is not recognized\");\n")
        .append("         }\n")
        .append("         return tfj.assignFromJavabuf(message);\n")
        .append("      } catch (Exception e) {\n")
        .append("         throw new RuntimeException(e);\n")
        .append("      }\n")
        .append("   }\n\n");
   }

   private static void createTranslator(Class<?> clazz, StringBuilder sb) throws Exception
   {
      createTranslatorToJavabuf(clazz, sb);
      createTranslatorFromJavabuf(clazz, sb);
   }

   private static void privateVariables(StringBuilder sb) {
      sb.append("   private static Map<Class<?>, TranslateToJavabuf> toJavabufMap = new HashMap<Class<?>, TranslateToJavabuf>();\n");
      sb.append("   private static Map<String, TranslateFromJavabuf> fromJavabufMap = new HashMap<String, TranslateFromJavabuf>();\n\n");
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
            "   private static AssignFromJavabuf fromJavabuf(Class<?> javaClass, FieldDescriptor fd) {\n" +
            "      try {\n" +
            "         AssignFromJavabuf assignFromJavabuf = (message, object) -> {\n" +
            "            try {\n" +
            "               if (isSuperClass(fd.getName())) {\n" +
            "                  String superClassName = javaClassToJavabufClass(javaClass.getSuperclass().getName());\n" +
            "                  TranslateFromJavabuf t = fromJavabufMap.get(superClassName);\n" +
            "                  FieldDescriptor sfd = getSuperField(message);\n" +
            "                  Message superMessage = (Message) message.getField(sfd);\n" +
            "                  t.assignExistingFromJavabuf(superMessage, object);\n" +
            "               } else {\n" +
            "                  final Field field = javaClass.getDeclaredField(fd.getName());\n" +
            "                  field.setAccessible(true);\n"+
            "                  if (fromJavabufMap.keySet().contains(field.getType())) {\n" +
            "                     Object obj = fromJavabufMap.get(field.getType()).assignFromJavabuf(message);\n" +
            "                     field.set(object, obj);\n" +
            "                  } else {\n" +
            "                     field.set(object, message.getField(fd));\n" +
            "                  }\n" +
            "               }\n" +
            "            } catch (Exception e) {\n" +
            "               throw new RuntimeException(e);\n" +
            "            }\n" +
            "         };\n" +
            "         return assignFromJavabuf;\n" +
            "      } catch (Exception e) {\n" +
            "         throw new RuntimeException(e);\n" +
            "      }\n" +
            "   }\n\n"
      );
      sb.append("   private static String javaClassToJavabufClass(String javaClassName) {\n")
        .append("      String javabufClassName = javaClassName.replace(\".\", \"_\");\n")
        .append("      int i = javabufClassName.lastIndexOf(\"_\");\n")
        .append("      javabufClassName = javabufClassName.substring(0, i) + \"___\" + javabufClassName.substring(i + 1);\n")
        .append("      return javabufClassName;\n")
        .append("   }\n\n");
      sb.append("   private static FieldDescriptor getSuperField(Message message) {\n")
        .append("      Map<FieldDescriptor, Object> map = message.getAllFields();\n")
        .append("      for (FieldDescriptor fd : map.keySet()) {\n")
        .append("         if (fd.getName().endsWith(\"___super\")) {\n")
        .append("            return fd;\n")
        .append("         }\n")
        .append("      }\n")
        .append("      return null;\n")
        .append("   }\n");
      sb.append("   private static Object messageToObject(Message message) throws ClassNotFoundException {\n")
        .append("      String messageClassName = message.getClass().getName();\n")
        .append("      int i = messageClassName.indexOf(\"___\");\n")
        .append("      String classname = messageClassName.substring(0, i).replaceAll(\"_\", \".\") + \".\" + messageClassName.substring(i + 2);\n")
        .append("      return Class.forName(classname);\n")
        .append("   }\n\n");
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
        .append("      private static DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);\n")
        .append("      private static List<AssignToJavabuf> assignList = new ArrayList<AssignToJavabuf>();\n\n")
        .append("      static {\n")
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

   private static void createTranslatorFromJavabuf(Class<?> clazz, StringBuilder sb)
   {
      String originalName = originalSimpleName(clazz.getName());
      sb.append("   static class ")
      .append(fqnify(clazz.getSimpleName())).append("_FromJavabuf implements TranslateFromJavabuf {\n")
      .append("      private static Descriptor descriptor = ").append(clazz.getCanonicalName()).append(".getDescriptor();\n")
      .append("      private static List<AssignFromJavabuf> assignList = new ArrayList<AssignFromJavabuf>();\n\n")
      .append("      static {\n")
      .append("         for (FieldDescriptor f : descriptor.getFields()) {\n")
      .append("            String name = f.getName();\n")
      .append("            if (name.endsWith(\"_\")) {\n")
      .append("               name = name.substring(0, name.length() - 1);\n")
      .append("            }\n")
      .append("            if (descriptor.findFieldByName(name) == null) {\n")
      .append("               continue;\n")
      .append("            }\n")
      .append("            assignList.add(fromJavabuf(").append(originalName).append(".class, descriptor.findFieldByName(name)));\n")
      .append("         }\n")
      .append("      }\n\n")
      .append("      public ").append(originalName).append(" assignFromJavabuf(Message message) {\n")
      .append("         ").append(originalName).append(" obj = new ").append(originalName).append("();\n")
      .append("         for (AssignFromJavabuf assignFrom : assignList) {\n")
      .append("            try {\n")
      .append("               assignFrom.assign(message, obj);\n")
      .append("            } catch (Exception e) {\n")
      .append("               throw new RuntimeException(e);\n")
      .append("            }\n")
      .append("         }\n")
      .append("         return obj;\n")
      .append("      }\n\n")
      .append("      public void assignExistingFromJavabuf(Message message, Object obj) {\n")
      .append("         for (AssignFromJavabuf assignFrom : assignList) {\n")
      .append("            try {\n")
      .append("               assignFrom.assign(message, obj);\n")
      .append("            } catch (Exception e) {\n")
      .append("               throw new RuntimeException(e);\n")
      .append("            }\n")
      .append("         }\n")
      .append("      }\n")
      .append("   }\n\n");
   }

   private static void finishClass(StringBuilder sb) {
      sb.append("}\n");
   }

   static private void writeTranslatorClass(String[] args, String translatorClass, StringBuilder sb) throws IOException {
      StringBuilder root = new StringBuilder("target/generatedSources");
      File dir = new File(root.toString());
      if(!dir.exists())
      {
         dir.mkdir();
      }
      String pkg = args[1].lastIndexOf(".") < 0 ? "" : args[1].substring(0, args[1].lastIndexOf("."));
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

   static private String fqnify(String s) {
      return s.replace(".", "_");
   }

   static private String dotify(String s) {
      return s.replace("_", ".");
   }
   
   static private String originalSimpleName(String s) {
      int i = s.lastIndexOf("___");
      return i < 0 ? s : s.substring(i + 3);
   }
}