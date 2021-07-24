package org.jboss.resteasy.test;

import org.junit.Test;

import com.google.protobuf.Message;

import io.grpc.examples.classes3.CC1;
import io.grpc.examples.classes3.CC2;
//import io.grpc.examples.classes3.CC_JavabufTranslator;
//io/grpc/classes/CC_JavabufTranslator.java
public class CC_test {
   
   @Test
   public void testCC2() {
      CC2 cc2 = new CC2(3);
//      CC_JavabufTranslator translator = new CC_JavabufTranslator();
//      Message message = translator.translateToJavabuf(cc1);
//      System.out.println("CC1 message:\n" + message.toString());
//      Message message = translator.translateToJavabuf(cc2);
//      System.out.println("CC2 message:\n" + message.toString());
      
   }
   
//   @Test
//   public void testC3() {
//      C3 c3 = new C3();
//      C_JavabufTranslator translator = new C_JavabufTranslator();
//      Message message = translator.translateToJavabuf(new C3());
//      System.out.println("C3 message: " + message.toString());
//   }

}
