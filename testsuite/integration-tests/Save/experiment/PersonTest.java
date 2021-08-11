package org.jboss.resteasy.experiment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.protobuf.ProtobufProvider;
import org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonTest
{
   private static final MediaType PROTOBUF_MEDIA_TYPE = new MediaType("application", "protobuf");
   private static Person ron = new Person(1, "ron", "ron@jboss.org");
   private static Person_proto.Person ron_proto = Person_proto.Person.newBuilder().setId(1).setName("ron").setEmail("ron@jboss.org").build();
   private static VeryBigPerson veryBigRon = PersonUtil.getVeryBigPerson("ron");
   private static VeryBigPerson_proto.VeryBigPerson veryBigRon_proto = PersonUtil.getVeryBigPerson_proto("ron");

   @Before
   public void before() {
//      JsonBindingProvider.setSize(0);
      ProtobufProvider.setSize(0);
      ProtobufProvider.getAssignFromMap().clear();
      ProtobufProvider.getAssignToMap().clear();
      ProtobufProvider.getMap().clear();
   }
   
   @Test
   public void testProtobufProviderPerson() throws Exception
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
      System.out.println("Person (protobuf):");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
      System.out.println("  bytes:  " + ProtobufProvider.getSize());
   }

   @Test
   public void testProtobufProviderPerson_proto() throws Exception
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
      System.out.println("  bytes:  " + ProtobufProvider.getSize());
   }

   @Test
   public void testJSONPerson() throws Exception
   {
      JsonBindingProvider provider = new JsonBindingProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(ron, Person.class, Person.class, null, MediaType.APPLICATION_JSON_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(Object.class, Person.class, null, MediaType.APPLICATION_JSON_TYPE, null, bais);
         Assert.assertEquals(ron, obj);
      }
      System.out.println("==========");
      System.out.println("Person (JSON):");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
//      System.out.println("  bytes:  " + JsonBindingProvider.getSize());
   }

   @Test
   public void testProtobufProviderVeryBigPerson() throws Exception
   {
      ProtobufProvider provider = new ProtobufProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(veryBigRon, VeryBigPerson.class, VeryBigPerson.class, null, PROTOBUF_MEDIA_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(VeryBigPerson.class, VeryBigPerson.class, null, PROTOBUF_MEDIA_TYPE, null, bais);
         Assert.assertEquals("ron", ((VeryBigPerson) obj).getS0());
      }
      System.out.println("==========");
      System.out.println("VeryBigPerson (protobuf):");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
      System.out.println("  bytes:  " + ProtobufProvider.getSize());
   }

   @Test
   public void testProtobufProviderVeryBigPerson_proto() throws Exception
   {
      ProtobufProvider provider = new ProtobufProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(veryBigRon_proto, veryBigRon_proto.getClass(), veryBigRon_proto.getClass(), null, PROTOBUF_MEDIA_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(veryBigRon_proto.getClass(), veryBigRon_proto.getClass(), null, PROTOBUF_MEDIA_TYPE, null, bais);
         Assert.assertEquals("ron", ((VeryBigPerson_proto.VeryBigPerson) obj).getS0());
      }
      System.out.println("==========");
      System.out.println("VeryBigPerson_proto.VeryBigPerson:");
      System.out.println("  count: " + count);
      System.out.println("  time:  " + (System.currentTimeMillis() - start));
      System.out.println("  bytes:  " + ProtobufProvider.getSize());
   }
   
   @Test
   public void testJSONVeryBigPerson() throws Exception
   {
      JsonBindingProvider provider = new JsonBindingProvider();
      int count = 10000000;
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         provider.writeTo(veryBigRon, VeryBigPerson.class, VeryBigPerson.class, null, MediaType.APPLICATION_JSON_TYPE, null, baos);
         byte[] bytes = baos.toByteArray();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Object obj = provider.readFrom(Object.class, veryBigRon.getClass(), null, MediaType.APPLICATION_JSON_TYPE, null, bais);
         Assert.assertEquals("ron", ((VeryBigPerson) obj).getS0());
      }
      System.out.println("==========");
      System.out.println("VeryBigPerson (JSON):");
      System.out.println("  count: " + count);
      System.out.println("   time:  " + (System.currentTimeMillis() - start));
//      System.out.println("  bytes:  " + JsonBindingProvider.getSize());
   }
}