package org.jboss.resteasy.plugins.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

public class ProtobufCompiler_save2
{
   public static Message compile(String directory, Object source) throws Exception
   {
      //      Class<?> clazz = source.getClass();
      //      Message message = null;
      //      Method getDefaultInstance = ProtobufProvider.getMap().get(clazz);
      //      if (getDefaultInstance != null)
      //      {
      //         message = (Message) getDefaultInstance.invoke(null);
      //      }
      //      else
      //      {
      //         String protoDescriptor = translateToProtobuf(clazz);
      //         String protoFilename = writeProtoDescriptor(directory, protoDescriptor, clazz);
      //         compileProtoDescriptor(directory, protoFilename);
      //         message = compileGeneratedJava(directory, clazz);
      //         System.out.println(message.getClass());  
      //      }
      Message message = getDefaultMessage(directory, source.getClass());
      //      System.out.println("desc: " + message.getDescriptorForType());
      //      return copyToProtobufClass(source, message);
      return copyToProtobufClass(source, message.getDescriptorForType());
      //      return copyToProtobufClass2(source, message);
   }

   public static Object decompile(String directory, Class<?> clazz, InputStream inputStream) throws Exception
   {
      //      String protoDescriptor = translateToProtobuf(clazz);
      //      String protoFilename = writeProtoDescriptor(directory, protoDescriptor, clazz);
      //      compileProtoDescriptor(directory, protoFilename);
      //      Message message = compileGeneratedJava(directory, clazz);
      Message message = getDefaultMessage(directory, clazz);
      final Message parsedMessage = message.getParserForType().parseFrom(inputStream);
      Descriptor descriptor = message.getDescriptorForType();
      final Object object = clazz.newInstance();
      //      List<FieldDescriptor> list = descriptor.getFields();
      //      DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
      //      for (FieldDescriptor fd : list)
      //      {
      //         System.out.println("field: " + fd);
      //         Method m = getGetMethod(from.getClass(), fd.getName());
      //         builder.setField(fd, m.invoke(from));
      //      }

      List<ProtobufProvider.AssignFrom> assigns = ProtobufProvider.getAssignFromMap().get(clazz);
      if (assigns != null)
      {
         for (ProtobufProvider.AssignFrom assign2 : assigns)
         {
            assign2.assign(parsedMessage, object);
         }
      }
      else
      {
         assigns = new ArrayList<ProtobufProvider.AssignFrom>();
         for (FieldDescriptor fd : descriptor.getFields())
         {
            final Field field = clazz.getDeclaredField(fd.getName());
            field.setAccessible(true);
            final Method m = getGetMethod(parsedMessage.getClass(), fd.getName());
            //         field.set(obj, m.invoke(parsedMessage));

            ProtobufProvider.AssignFrom assign2 = (msg, obj) ->
            {
               try
               {
                  field.set(obj, m.invoke(msg));
               }
               catch (Exception e)
               {
                  //
               }
            };

            assign2.assign(parsedMessage, object);
            assigns.add(assign2);
         }


         //      for (Field field : clazz.getDeclaredFields())
         //      {
         //         field.setAccessible(true);
         //         field.set(obj, descriptor.ge);
         //      }
         ProtobufProvider.getAssignFromMap().put(clazz, assigns);
      }
      return object;
   }

   private static Message getDefaultMessage(String directory, Class<?> clazz) throws Exception
   {
      Message message = null;
      Method getDefaultInstance = ProtobufProvider.getMap().get(clazz);
      if (getDefaultInstance != null)
      {
         message = (Message) getDefaultInstance.invoke(null);
      }
      else
      {
         String protoDescriptor = translateToProtobuf(clazz);
         String protoFilename = writeProtoDescriptor(directory, protoDescriptor, clazz);
         compileProtoDescriptor(directory, protoFilename);
         message = compileGeneratedJava(directory, clazz);
         //         System.out.println(message.getClass());  
      }
      return message;
   }

   private static String translateToProtobuf(Class<?> clazz)
   {
      AtomicInteger count = new AtomicInteger(1);
      StringBuilder sb = new StringBuilder();
      sb.append("syntax = \"proto3\";\n");
      String packageName = clazz.getPackage().getName();
      sb.append("package " + packageName + ";\n");
      sb.append("option java_package = \"" + packageName + "\";\n");
      sb.append("option java_outer_classname = \"" + clazz.getSimpleName() + "_proto\";\n");
      sb.append("message " + clazz.getSimpleName() + " {\n");

      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields)
      {
         //         System.out.println("type: " + field.getType().getName());
//         sb.append("  required ");
         sb.append(" ");
         switch (field.getType().getName())
         {
            case "int":
               sb.append("int32 ");
               break;

            case "java.lang.String":
               sb.append("string ");
               break;

            default:
               sb.append("Object ");
               break;
         }
         sb.append(field.getName() + " = " + count.getAndIncrement() + ";\n");
      }
      sb.append("}\n");
      //      System.out.println(sb.toString());
      return sb.toString();
   }

   private static String writeProtoDescriptor(String directory, String proto, Class<?> clazz) throws IOException
   {
      String filename = directory + "/" + clazz.getSimpleName() + "_proto";
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      writer.write(proto);
      writer.close();
      return filename;
   }

   private static void compileProtoDescriptor(String directory, String protoFilename) throws IOException, InterruptedException
   {
      // Command to create an external process 
      //         String command = "/home/rsigal/bin/protoc -I=/tmp/tmp --java_out=/tmp/tmp/ " + file;
      String command = "/home/rsigal/bin/protoc -I=" + directory + " --java_out=" + directory + " " + protoFilename;
      //         String command = "/home/rsigal/bin/protoc -I=/home/rsigal/tmp/git.master.proto/Resteasy/providers/resteasy-protobuf-provider/src/test/resources/ --java_out=/home/rsigal/tmp/git.master.proto/Resteasy/providers/resteasy-protobuf-provider/target/classes/ " + file;
      //      System.out.println(command);
      // Running the above command 
      Runtime run  = Runtime.getRuntime(); 
      Process proc = run.exec(command);
      synchronized (proc)
      {
         proc.wait(2000);
      }
//            System.out.println("status: " + proc.exitValue());
      return;
   }

   // Message defaultPerson = new ProtobufCompiler().compile(directory + "/target", directory + "/target/org/jboss/resteasy/test/Person_proto.java", "org.jboss.resteasy.test.Person_proto", "Person");

   private static Message compileGeneratedJava(String directory, Class<?> clazz)
   { 
      URLClassLoader classLoader = null;
      StandardJavaFileManager fileManager = null;

      //      String protoClassname = classToProtoClassname(clazz);
      File file = new File(directory + classnameToFilename("/" + clazz.getName() + "_proto") + ".java");
      Message message = null;

      try
      {
         /** Compilation Requirements *********************************************************************************************/
         DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         fileManager = compiler.getStandardFileManager(diagnostics, null, null);

         List<String> optionList = new ArrayList<String>();
         optionList.add("-classpath");
         //         optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "/tmp/tmp");
         optionList.add(System.getProperty("java.class.path") + File.pathSeparator + directory +
               File.pathSeparator + "/home/rsigal/tmp/git.master.proto/Resteasy/testsuite/integration-tests/target/test-server/wildfly-19.0.0.Final/modules/system/layers/base/org/jboss/resteasy/resteasy-jaxrs/main/protobuf-java-3.12.0.jar");

         Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
         JavaCompiler.CompilationTask task = compiler.getTask(
               null, 
               fileManager, 
               diagnostics, 
               optionList, 
               null, 
               compilationUnit);
         /********************************************************************************************* Compilation Requirements **/
         if (task.call())
         {
            // Create a new custom class loader, pointing to the directory that contains the compiled
            // classes, this should point to the top of the package structure!
            //            URL url = new File("/tmp/tmp").toURI().toURL();
            URL url = new File(directory).toURI().toURL();
            //            URL protobufUrl = new File("/home/rsigal/tmp/git.master.proto/Resteasy/testsuite/integration-tests/target/test-server/wildfly-19.0.0.Final/modules/system/layers/base/org/jboss/resteasy/resteasy-jaxrs/main/protobuf-java-3.12.0.jar").toURI().toURL();
            //            System.out.println("url: " + url);
            //                  classLoader = new URLClassLoader(new URL[]{new File("/tmp").toURI().toURL()});
            //            classLoader = new URLClassLoader(new URL[] {url, protobufUrl}, Thread.currentThread().getContextClassLoader());
            classLoader = new URLClassLoader(new URL[] {url}, Thread.currentThread().getContextClassLoader());

            // Load the class from the classloader by name....
            //                  Class<?> loadedClass = classLoader.loadClass("testcompile.HelloWorld");
            //            loadedClass = classLoader.loadClass("HelloWorld");
            //            loadedClass = classLoader.loadClass(protoClassname);
            //            loadedClass = classLoader.loadClass(clazz.getName() + "_proto");
            Class<?> subclass = classLoader.loadClass(classToProtoClassname(clazz));
            //            System.out.println("subclass: " + subclass);
            //            Class<?>[] classes = loadedClass.getClasses();
            //            System.out.println(classes.length);
            //            Method getDefaultInstance = loadedClass.getMethod("getDefaultInstance");
            Method getDefaultInstance = subclass.getMethod("getDefaultInstance");
            ProtobufProvider.getMap().put(clazz, getDefaultInstance);
            message = (Message) getDefaultInstance.invoke(null);
            message.getDescriptorForType();
            //            System.out.println("desc: " + message.getDescriptorForType());
            //            System.out.println();
            /************************************************************************************************* Load and execute **/
         }
         else
         {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
            {
               System.out.format("Error on line %d in %s%n: %s",
                     diagnostic.getLineNumber(),
                     diagnostic.getSource().toUri(),
                     diagnostic.getMessage(null));
            }
         }
         return message;
      } catch (Exception exp)
      {
         exp.printStackTrace();
         return null;
      }
      finally
      {
         try
         {
            if (fileManager != null)
            {
               fileManager.close();  
            }
            if (classLoader != null)
            {
               classLoader.close();  
            }
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   //   interface Assign
   //   {
   //      public void assign(DynamicMessage.Builder builder, Method get, FieldDescriptor fd, Object from);
   //   }

   //   private static Message copyToProtobufClass(Object from, Message to) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
   private static Message copyToProtobufClass(Object from, Descriptor descriptor) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
   {
      //      Descriptor descriptor = to.getDescriptorForType();
      List<ProtobufProvider.AssignTo> assigns = ProtobufProvider.getAssignToMap().get(from.getClass());
      if (assigns != null)
      {
         DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
         for (ProtobufProvider.AssignTo assign : assigns)
         {
            assign.assign(from, builder);
         }
         return builder.build();
      }
      else
      {
         List<FieldDescriptor> list = descriptor.getFields();
         final DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
         List<ProtobufProvider.AssignTo> assignList = new ArrayList<ProtobufProvider.AssignTo>();
         for (final FieldDescriptor fd : list)
         {
            //         System.out.println("field: " + fd);
            final Method m = getGetMethod(from.getClass(), fd.getName());
            //         builder.setField(fd, m.invoke(from));

            ProtobufProvider.AssignTo assign = (obj, messageBuilder) ->
            {
               try
               {
                  messageBuilder.setField(fd, m.invoke(obj));
               }
               catch (Exception e)
               {
                  //
               }
            };
            assign.assign(from, builder);
            assignList.add(assign);
         }
         ProtobufProvider.getAssignToMap().put(from.getClass(), assignList);
         DynamicMessage dm = builder.build();
         return dm;
      }
   }

   private static Message copyToProtobufClass2(Object from, Message message) throws Exception
   {
      Field[] fields = message.getClass().getFields();
      for (Field field : fields)
      {
         Method get = getGetMethod(from.getClass(), field.getName());
         Method set = getSetMethod(message.getClass(), field.getName(), field.getType());
         set.invoke(message, get.invoke(from));
      }
      return message;
   }

   private static String classnameToFilename(String classname)
   {
      return classname.replace(".", "/");
   }

   private static String filenameToClassname(String filename)
   {
      return filename.replace("/", ".");
   }

   private static String classToProtoClassname(Class<?> clazz)
   {
      String classname = clazz.getName();
      String simpleName = clazz.getSimpleName();
      int n = classname.lastIndexOf(".");
      String prefix = n > -1 ? classname.substring(0, n) : "";
      return prefix + "." + simpleName + "_proto" + "$" + simpleName;
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

   private static Method getSetMethod(Class<?> clazz, String fieldName, Class<?> fieldClass) throws NoSuchMethodException
   {
      StringBuilder sb = new StringBuilder("set");
      sb.append(Character.toUpperCase(fieldName.charAt(0)));
      if (fieldName.length() > 1)
      {
         sb.append(fieldName.substring(1));
      }
      return clazz.getMethod(sb.toString(), fieldClass);
   }
}
