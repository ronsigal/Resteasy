package org.jboss.resteasy.plugins.protobuf;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaToProtobufTranslator
{
   private AtomicInteger count = new AtomicInteger(1);
   
//   public static void main(String[] args)
//   {
//      new JavaToProtobufTranslator().translate(Person.class);
//   }

   public String translate(Class<?> clazz)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("syntax = \"proto2\";\n");
      String packageName = clazz.getPackage().getName();
      sb.append("package " + packageName + ";\n");
      sb.append("option java_package = \"" + packageName + "\";\n");
      sb.append("option java_outer_classname = \"" + clazz.getSimpleName() + "_proto\";\n");
      sb.append("message " + clazz.getSimpleName() + " {\n");

      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields)
      {
//         System.out.println("type: " + field.getType().getName());
         sb.append("  required ");
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
}
