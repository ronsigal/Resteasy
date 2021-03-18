package org.jboss.resteasy.plugins.protobuf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.protobuf.Message;

public class ProtobufCompiler_save
{
   private URLClassLoader classLoader;
   private Class<?> loadedClass;
   private Message defaultPerson;

   public static void main(String[] args)
   {
      new ProtobufCompiler_save().compileHW("");;
   }

//   public Class<?> compile(String directory, String filename, String className)
   public Message compile(String directory, String filename, String className)
   {
      File file = new File(filename);

      try {
         /** Compilation Requirements *********************************************************************************************/
         DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

         // This sets up the class path that the compiler will use.
         // I've added the .jar file that contains the DoStuff interface within in it...
         List<String> optionList = new ArrayList<String>();
         optionList.add("-classpath");
//         optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "/tmp/tmp");
         optionList.add(System.getProperty("java.class.path") + File.pathSeparator + directory);
         
         Iterable<? extends JavaFileObject> compilationUnit
         = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
         JavaCompiler.CompilationTask task = compiler.getTask(
               null, 
               fileManager, 
               diagnostics, 
               optionList, 
               null, 
               compilationUnit);
         /********************************************************************************************* Compilation Requirements **/
         if (task.call()) {
            /** Load and execute *************************************************************************************************/
            System.out.println("Yipe");
            // Create a new custom class loader, pointing to the directory that contains the compiled
            // classes, this should point to the top of the package structure!
//            URL url = new File("/tmp/tmp").toURI().toURL();
            URL url = new File(directory).toURI().toURL();
            System.out.println("url: " + url);
            //                  classLoader = new URLClassLoader(new URL[]{new File("/tmp").toURI().toURL()});
            classLoader = new URLClassLoader(new URL[] {url});
            // Load the class from the classloader by name....
            //                  Class<?> loadedClass = classLoader.loadClass("testcompile.HelloWorld");
//            loadedClass = classLoader.loadClass("HelloWorld");
            loadedClass = classLoader.loadClass(className);
            Class<?> subclass = classLoader.loadClass(className + "$Person");
            System.out.println("subclass: " + subclass);
            Class<?>[] classes = loadedClass.getClasses();
            System.out.println(classes.length);
            Method getDefaultInstance = subclass.getMethod("getDefaultInstance");
            defaultPerson = (Message) getDefaultInstance.invoke(null);
            System.out.println();
            // Create a new instance...
//            Object obj = loadedClass.newInstance();
//            // Santity check
//            //                  if (obj instanceof DoStuff) {
//            //                      // Cast to the DoStuff interface
//            //                      DoStuff stuffToDo = (DoStuff)obj;
//            //                      // Run it baby
//            //                      stuffToDo.doStuff();
//            //                  }
//            Method method = obj.getClass().getMethod("doStuff");
//            method.invoke(obj);
            /************************************************************************************************* Load and execute **/
         } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
               System.out.format("Error on line %d in %s%n",
                     diagnostic.getLineNumber(),
                     diagnostic.getSource().toUri());
            }
         }
         fileManager.close();
//         return loadedClass;
         return defaultPerson;
      } catch (Exception exp) {
         exp.printStackTrace();
         return null;
      }
      finally
      {
         try
         {
            classLoader.close();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   public void compileHW(String file)
   {
      StringBuilder sb = new StringBuilder(64);
      //      sb.append("package testcompile;\n");
      //      sb.append("public class HelloWorld implements inlinecompiler.InlineCompiler.DoStuff {\n");
      sb.append("public class HelloWorld {\n");
      sb.append("    public void doStuff() {\n");
      sb.append("        System.out.println(\"Hello world\");\n");
      sb.append("    }\n");
      sb.append("}\n");

      File helloWorldJava = new File("/tmp/HelloWorld.java");
      if (helloWorldJava.getParentFile().exists() || helloWorldJava.getParentFile().mkdirs()) {

         try {
            Writer writer = null;
            try {
               writer = new FileWriter(helloWorldJava);
               writer.write(sb.toString());
               writer.flush();
            } finally {
               try {
                  writer.close();
               } catch (Exception e) {
               }
            }

            /** Compilation Requirements *********************************************************************************************/
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<String>();
            optionList.add("-classpath");
            optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

            Iterable<? extends JavaFileObject> compilationUnit
            = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(helloWorldJava));
            JavaCompiler.CompilationTask task = compiler.getTask(
                  null, 
                  fileManager, 
                  diagnostics, 
                  optionList, 
                  null, 
                  compilationUnit);
            /********************************************************************************************* Compilation Requirements **/
            if (task.call()) {
               /** Load and execute *************************************************************************************************/
               System.out.println("Yipe");
               // Create a new custom class loader, pointing to the directory that contains the compiled
               // classes, this should point to the top of the package structure!
               URL url = new File("/tmp").toURI().toURL();
               System.out.println("url: " + url);
               //                  classLoader = new URLClassLoader(new URL[]{new File("/tmp").toURI().toURL()});
               classLoader = new URLClassLoader(new URL[] {url});
               // Load the class from the classloader by name....
               //                  Class<?> loadedClass = classLoader.loadClass("testcompile.HelloWorld");
               Class<?> loadedClass = classLoader.loadClass("HelloWorld");
               // Create a new instance...
               Object obj = loadedClass.newInstance();
               // Santity check
               //                  if (obj instanceof DoStuff) {
               //                      // Cast to the DoStuff interface
               //                      DoStuff stuffToDo = (DoStuff)obj;
               //                      // Run it baby
               //                      stuffToDo.doStuff();
               //                  }
               Method method = obj.getClass().getMethod("doStuff");
               method.invoke(obj);
               /************************************************************************************************* Load and execute **/
            } else {
               for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                  System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getSource().toUri());
               }
            }
            fileManager.close();
         } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException exp) {
            exp.printStackTrace();
         }
         finally
         {
            try
            {
               classLoader.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }

      }
   }


   public static interface DoStuff {

      public void doStuff();
   }
}
