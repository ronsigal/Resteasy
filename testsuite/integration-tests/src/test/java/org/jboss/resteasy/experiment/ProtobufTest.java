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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProtobufTest {

   private static UndertowJaxrsServer server;
   private static Client client;

   @Path("/")
   public static class TestResource {

      @POST
      @Path("json")
      @Consumes("application/json")
      @Produces("application/json")
      public Person json(Person person) {
         person.setEmail("a@b");
         person.setId(3);
         person.setName("tanicka");
         return person;
      }

      @POST
      @Path("protobuf")
      @Consumes("application/protobuf")
      @Produces("application/protobuf")
      public Person proto(Person person) {
         person.setEmail("a@b");
         person.setId(3);
         person.setName("tanicka");
         return person;
      }

      @POST
      @Path("big/json")
      @Consumes("application/json")
      @Produces("application/json")
      public BigPerson bigJson(BigPerson person) throws Exception {
         BigPerson bp = getBigPerson(3, "tanicka", "a@b");
         return bp;
      }

      @POST
      @Path("big/protobuf")
      @Consumes("application/protobuf")
      @Produces("application/protobuf")
      public BigPerson bigProto(BigPerson person) throws Exception {
         BigPerson bp = getBigPerson(3, "tanicka", "a@b");
         return bp;
      }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
      client = ClientBuilder.newClient();
      client.register(ProtobufProvider.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
      server.stop();
   }

   //   @Test
   public void testProto() {
      doTest("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTest("protobuf");
      }
      System.out.println("protobuf: " + (System.currentTimeMillis() - start));
   }

   //   @Test
   public void testJSON() {
      doTest("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doTest("json");
      }
      System.out.println("json: " + (System.currentTimeMillis() - start));
   }

   private void doTest(String transport)
   {
      Builder request = client.target("http://localhost:8081/" + transport).request();
      Person ron = new Person(1, "ron", "ron@jboss.org");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      //      System.out.println("status: " + response.getStatus());
      Person person = response.readEntity(Person.class);
      Person tanicka = new Person(3, "tanicka", "a@b");
      Assert.assertEquals(tanicka, person);
   }

   //   @Test
   public void testBigProto() throws Exception {
      doBigTest("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigTest("protobuf");
      }
      System.out.println("protobuf: " + (System.currentTimeMillis() - start));
   }

   //   @Test
   public void testBigJSON() throws Exception {
      doBigTest("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigTest("json");
      }
      System.out.println("json: " + (System.currentTimeMillis() - start));
   }

   private void doBigTest(String transport) throws Exception
   {
      Builder request = client.target("http://localhost:8081/big/" + transport).request();
      BigPerson ron = getBigPerson(1, "ron", "ron@jboss");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      BigPerson person = response.readEntity(BigPerson.class);
      BigPerson tanicka = getBigPerson(3, "tanicka", "a@b");
      Assert.assertEquals(tanicka.getName(), person.getName());
   }
   
//   @Test
   public void testBigJSONRemote() throws Exception {
      doBigTest("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigRemote("json");
      }
      System.out.println("json: " + (System.currentTimeMillis() - start));
      System.out.println("json size:     " + JsonBindingProvider.getSize());
   }
   
//   @Test
   public void testBigProtoRemote() throws Exception {
      doBigTest("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doBigRemote("protobuf");
      }
      System.out.println("protobuf: " + (System.currentTimeMillis() - start));
      System.out.println("protobuf size: " + ProtobufProvider.getSize());
   }

   private void doBigRemote(String transport) throws Exception {
      Builder request = client.target("http://httpbin.org/post").request().accept(MediaType.APPLICATION_JSON_TYPE);
      BigPerson ron = getBigPerson(1, "ron", "ron@jboss");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      BigPerson person = response.readEntity(BigPerson.class);
      Assert.assertEquals(ron.getName(), person.getName());
   }
   
 int count = 1000;
   
   @Test
   public void testVeryBigJSONRemote() throws Exception {
      System.out.println("count: " + count);
      JsonBindingProvider.setSize(0);
      doVeryBigRemote("json");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doVeryBigRemote("json");
      }
      System.out.println("json:          " + (System.currentTimeMillis() - start));
      System.out.println("json size:     " + JsonBindingProvider.getSize());
   }
   
   @Test
   public void testVeryBigProtoRemote() throws Exception {
      System.out.println("count: " + count);
      ProtobufProvider.setSize(0);
      doVeryBigRemote("protobuf");
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         doVeryBigRemote("protobuf");
      }
      System.out.println("protobuf:      " + (System.currentTimeMillis() - start));
      System.out.println("protobuf size: " + ProtobufProvider.getSize());
   }

   private void doVeryBigRemote(String transport) throws Exception {
      System.out.println("doVeryBigRemote");
      Builder request = client.target("http://httpbin.org/post").request().accept(MediaType.APPLICATION_JSON_TYPE);
      VeryBigPerson ron = getVeryBigPerson(1, "ron", "ron@jboss");
      Response response = request.post(Entity.entity(ron, "application/" + transport));
      response.readEntity(String.class);
   }

   private static String abc = "abcdefghijklmnopqrstuvwxyz";

   private static BigPerson getBigPerson(int id, String name, String email) throws Exception
   {  
      BigPerson bp = new BigPerson();
      bp.setId(id);
      bp.setName(name);
      bp.setEmail(email);
      for (int i = 0; i < 25; i++)
      {
         Method m = BigPerson.class.getMethod("setS" + i, String.class);
         m.invoke(bp, abc.substring(i) + abc.substring(0, i));
      }
      return bp;
   }
   
   private static VeryBigPerson getVeryBigPerson(int id, String name, String email) throws Exception
   {  
      VeryBigPerson bp = new VeryBigPerson();
      Field[] fields = VeryBigPerson.class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++)
      {
         fields[i].setAccessible(true);
         int position = i % 25;
         fields[i].set(bp, abc.substring(position) + abc.substring(0, position));
      }
      return bp;
   }
}
