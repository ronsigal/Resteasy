package org.jboss.resteasy.plugins.protobuf;

import java.lang.reflect.Method;
import java.util.List;

import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;

public class ProtobufDecompiler
{
   public static Object decompile(Message message, Class<?> clazz) throws Exception
   {
      System.out.println("message: " + message.getClass());
      for (Method m : message.getClass().getMethods())
      {
         System.out.println(m.getName());
      }
      DynamicMessage dm;
      Object obj = clazz.getConstructor().newInstance();
      Descriptor descriptor = message.getDescriptorForType();
      List<FieldDescriptor> list = descriptor.getFields();
      
      for (FieldDescriptor fd : list)
      {
         System.out.println(fd.getName());
         System.out.println(message.getField(fd));
         Class<?> fieldClass = null;
         switch (fd.getJavaType())
         {
            case INT:
               fieldClass = int.class;
               break;
               
            case STRING:
               fieldClass = String.class;
               break;
               
            default:
               fieldClass = Object.class;
               break;
         }
         
         Method set = getSetMethod(clazz, fd.getName(), fieldClass);
         set.invoke(obj, message.getField(fd));
      }
      return obj;
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
