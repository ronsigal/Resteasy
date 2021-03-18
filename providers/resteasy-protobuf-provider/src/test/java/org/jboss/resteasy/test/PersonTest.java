package org.jboss.resteasy.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.experiment.Person_proto;
import org.jboss.resteasy.plugins.protobuf.ProtobufCompiler;
import org.jboss.resteasy.plugins.protobuf.ProtobufProvider;
import org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider;
import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.Message;

public class PersonTest
{
   private static final MediaType PROTOBUF_MEDIA_TYPE = new MediaType("application", "protobuf");
   private static Person ron = new Person(1, "ron", "ron@jboss.org");
   private static Person_proto.Person ron_proto = Person_proto.Person.newBuilder().setId(1).setName("ron").setEmail("ron@jboss.org").build();

   //   @Test
   public void testProtobufProvider() throws Exception
   {
      ProtobufProvider provider = new ProtobufProvider();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      provider.writeTo(ron, Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, baos);
      byte[] bytes = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      Object obj = provider.readFrom(Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, bais);
      Assert.assertEquals(ron, obj);
      baos = new ByteArrayOutputStream();
      provider.writeTo(ron, Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, baos);
      bytes = baos.toByteArray();
      bais = new ByteArrayInputStream(bytes);
      obj = provider.readFrom(Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, bais);
      Assert.assertEquals(ron, obj);
   }

//   @Test
   public void testProtobufProviderLoop() throws Exception
   {
      ProtobufProvider provider = new ProtobufProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(ron, Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(Person.class, Person.class, null, PROTOBUF_MEDIA_TYPE, null, bais);
         Assert.assertEquals(ron, obj);
      }
      System.out.println("==========");
      System.out.println("Person:");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
   }
   
//   @Test
   public void testProtobufMessageLoop() throws Exception
   {
      ProtobufProvider provider = new ProtobufProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(ron_proto, ron_proto.getClass(), ron_proto.getClass(), null, PROTOBUF_MEDIA_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(Person_proto.Person.class, Person_proto.Person.class, null, PROTOBUF_MEDIA_TYPE, null, bais);
         Assert.assertEquals(ron_proto, obj);
      }
      System.out.println("==========");
      System.out.println("Person_proto.Person:");
      System.out.println("  count: " + count);
      System.out.println("  time:  " + (System.currentTimeMillis() - start));
   }
   
   @Test
   public void testJSONLoop() throws Exception
   {
      JsonBindingProvider provider = new JsonBindingProvider();
      int count = 100000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(ron, Person.class, Person.class, null, MediaType.APPLICATION_JSON_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(Object.class, Object.class, null, MediaType.APPLICATION_JSON_TYPE, null, bais);
//         Assert.assertEquals(ron, obj);
      }
      System.out.println("==========");
      System.out.println("Person:");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
   }
}