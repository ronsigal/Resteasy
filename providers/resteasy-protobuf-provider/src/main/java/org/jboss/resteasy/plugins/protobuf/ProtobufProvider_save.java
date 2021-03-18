package org.jboss.resteasy.plugins.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

@Provider
@Produces("application/protobuf")
@Consumes("application/protobuf")
public class ProtobufProvider_save implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
//   public static void main(String[] args)
//   {
//      compile("tmp.proto");
//      Class<?> clazz = new ProtobufCompiler().compile("/tmp/tmp/com/example/tutorial/AddressBookProtos.java", "com.example.tutorial.AddressBookProtos");
//      try
//      {
////         GeneratedMessageV3 gm = (GeneratedMessageV3) clazz.newInstance();
//         Class<?>[] classes = clazz.getClasses();
//         Class<?> subclazz = findClass(classes, clazz.getCanonicalName() + "$Person");
//         Method newBuilder = subclazz.getMethod("newBuilder");
//         Person.Builder personBuilder = (Person.Builder) newBuilder.invoke(null);
//         personBuilder.setEmail("a@b.c");
//         personBuilder.setName("person");
//         personBuilder.setId(3);
//         Person person = personBuilder.build();
////         subclazz.newInstance();
////         subclazz = findClass(subclazz.getClasses(), clazz.getCanonicalName() + "$Person$Builder");
////         Object obj = clazz.newInstance();
////         ob
////         Descriptor desc = obj.getDescriptorForType();
////         System.out.println("desc: " + desc.getName());
//         System.out.println(person);
//         Message m = (Message) person;
//         System.out.println("m: " + m);
//         Method getDefaultInstance = subclazz.getMethod("getDefaultInstance");
//         Message defaultPerson = (Message) getDefaultInstance.invoke(null);
//         System.out.println("defaultPerson" + defaultPerson);
//         Descriptor descriptor = defaultPerson.getDescriptorForType();
//         List<FieldDescriptor> list = descriptor.getFields();
//         DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
//         for (FieldDescriptor fd : list)
//         {
//            System.out.println(fd);
//            Descriptors.FieldDescriptor.Type type = fd.getType();
//            switch (type)
//            {
//               case INT32:
//                  builder.setField(fd, 3);
//                  break;
//                  
//               case STRING:
//                  builder.setField(fd, "abc");
//                  break;
//                  
//               default:
//                  break;
//            }
//         }
//         DynamicMessage dm = builder.build();
//         System.out.println("dm: " + dm);
//         
//      }
//      catch (Exception e)
//      {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
//   }
   
   private static Class<?> findClass(Class<?>[] classes, String className)
   {
      for (Class<?> c : classes)
      {
         System.out.println(c.getName());
         if (className.equals(c.getName()))
         {
            return c;
         }
      }
      return null;
   }
   
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
               throws IOException, WebApplicationException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
               throws IOException, WebApplicationException
   {
      // TODO Auto-generated method stub
      return null;
   }

   private static void compile(String file)
   {
      try
      { 
          // Command to create an external process 
          String command = "/home/rsigal/bin/protoc -I=/tmp/tmp --java_out=/tmp/tmp/ " + file;

          // Running the above command 
          Runtime run  = Runtime.getRuntime(); 
          Process proc = run.exec(command); 
      } 
      catch (IOException e) 
      { 
          e.printStackTrace(); 
      } 
   }
}
