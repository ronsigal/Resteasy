package org.jboss.resteasy.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

//import io.grpc.examples.classes.C1;
//import io.grpc.examples.classes.C_proto.io_grpc_examples_classes_C1;

public class Translator {
//   public static AssignTo toJavabuf(Class<?> javaClass, FieldDescriptor fd) throws Exception {
//      final Field field = javaClass.getDeclaredField(fd.getName());
//      field.setAccessible(true);
//      AssignTo assignTo = (obj, messageBuilder) -> {
//         try {
//            messageBuilder.setField(fd, field.get(obj));
//         } catch (Exception e) {
//            //
//         }
//      };
//      return assignTo;
//   }
//}
//
//interface AssignTo
//{
//  public void assign(Object from, DynamicMessage.Builder builder);
//}
//
//interface AssignFrom
//{
//  public void assign(Message message, Object object);
//}
//   
//class io_grpc_examples_classes_C1$$$ToJavabuf {
//   private static Descriptor descriptor = io_grpc_examples_classes_C1.getDescriptor();
//   private static List<FieldDescriptor> fieldDescriptors = descriptor.getFields();
//   private static DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
//   private static List<AssignTo> assignList = new ArrayList<AssignTo>();
//   
//   static {
//      for (final FieldDescriptor fd : fieldDescriptors) {
//         assignList.add(Translator.toJavabuf(io_grpc_examples_classes_C1.class, fd));
//      }
//   }
//   
//   public static DynamicMessage assignToJavabuf(C1 c1) {
//      for (AssignTo assignTo : assignList) {
//         assignTo.assign(c1, builder);
//      }
//      return builder.build();
//   }
}
