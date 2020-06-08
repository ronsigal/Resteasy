package org.jboss.resteasy.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.protobuf.ProtobufProvider;
import org.junit.Assert;
import org.junit.Test;

public class PersonTest
{
   private static final MediaType PROTOBUF_MEDIA_TYPE = new MediaType("application", "protobuf");
   private static Person ron = new Person(1, "ron", "ron@jboss.org");
   @Test
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
}