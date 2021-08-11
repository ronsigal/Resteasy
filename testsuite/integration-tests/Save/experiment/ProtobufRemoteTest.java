package org.jboss.resteasy.experiment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.infinispan.commons.dataconversion.MediaType;
import org.jboss.resteasy.plugins.protobuf.ProtobufProvider;
import org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProtobufRemoteTest {

   private static Client client;
   private static Person ron = new Person(1, "ron", "ron@jboss.org");
   private static Person_proto.Person ron_proto = Person_proto.Person.newBuilder().setId(1).setName("ron").setEmail("ron@jboss.org").build();
   private static VeryBigPerson veryBigRon = PersonUtil.getVeryBigPerson("ron");
   private static VeryBigPerson_proto.VeryBigPerson veryBigRon_proto = PersonUtil.getVeryBigPerson_proto("ron");


   @BeforeClass
   public static void init() throws Exception {
      client = ClientBuilder.newClient();
      client.register(ProtobufProvider.class);
   }

   @Before
   public void before() throws Exception {
//      JsonBindingProvider.setSize(0);
      ProtobufProvider.setSize(0);
      ProtobufProvider.getAssignFromMap().clear();
      ProtobufProvider.getAssignToMap().clear();
      ProtobufProvider.getMap().clear();
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
   }

   @Test
   public void testJSONPerson() throws Exception {
      //      doTestPerson(ron, "json");
      System.out.println("==========\ncount: " + count);
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTestPerson(ron, "json");
      }
      System.out.println("Person (JSON) time:  " + (System.currentTimeMillis() - start));
//      System.out.println("Person (JSON) bytes: " + JsonBindingProvider.getSize());
   }

   @Test
   public void testProtobufPerson() throws Exception {
      //      doTestPerson(ron, "protobuf");
      System.out.println("==========\ncount: " + count);
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTestPerson(ron, "protobuf");
      }
      System.out.println("Person (protobuf) time:  " + (System.currentTimeMillis() - start));
      System.out.println("Person (protobuf) bytes: " + ProtobufProvider.getSize());
   }

   @Test
   public void testProtobufPerson_proto() throws Exception {
      //    doTestPerson(ron_proto, "protobuf");
      System.out.println("==========\ncount: " + count);
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTestPerson(ron_proto, "protobuf");
      }
      System.out.println("Person_proto (protobuf) time:  " + (System.currentTimeMillis() - start));
      System.out.println("Person_proto (protobuf) bytes: " + ProtobufProvider.getSize());
   }

   private void doTestPerson(Object entity, String transport) throws Exception {
      Builder request = client.target("http://httpbin.org/post").request().accept(MediaType.APPLICATION_JSON_TYPE);
      Response response = request.post(Entity.entity(entity, "application/" + transport));
      Person person = response.readEntity(Person.class);
//      Assert.assertEquals(entity, person);
   }

   int count = 10000;

   @Test
   public void testJSONVeryBigPerson() throws Exception {
      System.out.println("==========\ncount: " + count);
      //      doVeryBigRemote(veryBigRon, "json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doVeryBigRemote(veryBigRon, "json");
      }
      System.out.println("VeryBigPerson (JSON) time:  " + (System.currentTimeMillis() - start));
//      System.out.println("VeryBigPerson (JSON) bytes: " + JsonBindingProvider.getSize());
   }

   @Test
   public void testProtobufVeryBigPerson() throws Exception {
      System.out.println("==========\ncount: " + count);
      ProtobufProvider.setSize(0);
      //      doVeryBigRemote(-1, "protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doVeryBigRemote(veryBigRon, "protobuf");
      }
      System.out.println("VeryBigPerson (protobuf) time:  " + (System.currentTimeMillis() - start));
      System.out.println("VeryBigPerson (protobuf) bytes: " + ProtobufProvider.getSize());
   }

   @Test
   public void testProtobufVeryBigPerson_proto() throws Exception {
      System.out.println("==========\ncount: " + count);
      ProtobufProvider.setSize(0);
      //      doVeryBigRemote(veryBigRon_proto, "protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doVeryBigRemote(veryBigRon_proto, "protobuf");
      }
      System.out.println("VeryBigPerson_proto (protobuf) time:  " + (System.currentTimeMillis() - start));
      System.out.println("VeryBigPerson_proto (protobuf) bytes: " + ProtobufProvider.getSize());
   }

   private void doVeryBigRemote(Object entity, String transport) throws Exception {
      Builder request = client.target("http://httpbin.org/post").request().accept(MediaType.APPLICATION_JSON_TYPE);
      Response response = request.post(Entity.entity(entity, "application/" + transport));
      response.readEntity(String.class);
   }
}
