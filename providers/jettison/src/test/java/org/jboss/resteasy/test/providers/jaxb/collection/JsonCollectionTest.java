package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.core.messagebody.WriterUtility;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonCollectionTest extends BaseResourceTest
{
   private static Client client;
   
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Foo
   {
      @XmlAttribute
      private String test;

      public Foo()
      {
      }

      public Foo(String test)
      {
         this.test = test;
      }

      public String getTest()
      {
         return test;
      }

      public void setTest(String test)
      {
         this.test = test;
      }
   }

   @XmlRootElement(name = "foo", namespace = "http://foo.com")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class NamespacedFoo
   {
      @XmlAttribute
      private String test;

      public NamespacedFoo()
      {
      }

      public NamespacedFoo(String test)
      {
         this.test = test;
      }

      public String getTest()
      {
         return test;
      }

      public void setTest(String test)
      {
         this.test = test;
      }
   }

   @Path("/")
   public static class MyResource
   {
      @Path("/array")
      @Produces("application/json;charset=UTF-8")
      @GET
      public Foo[] get() throws Exception
      {
         Foo[] foo = {new Foo("bill{"), new Foo("monica\"}")};
         System.out.println("START");
         System.out.println(WriterUtility.asString(foo, "application/json"));
         System.out.println("FINISH");
         return foo;
      }

      @Path("/array")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public Foo[] array(Foo[] foo)
      {
         Assert.assertEquals(2, foo.length);
         Assert.assertEquals(foo[0].getTest(), "bill{");
         Assert.assertEquals(foo[1].getTest(), "monica\"}");
         return foo;
      }

      @Path("/list")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public List<Foo> list(List<Foo> foo)
      {
         System.out.println("POST LIST");
         Assert.assertEquals(2, foo.size());
         Assert.assertEquals(foo.get(0).getTest(), "bill{");
         Assert.assertEquals(foo.get(1).getTest(), "monica\"}");
         return foo;
      }


      @Path("/empty/array")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public Foo[] emptyArray(Foo[] foo)
      {
         Assert.assertEquals(0, foo.length);
         return foo;
      }

      @Path("/empty/list")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public List<Foo> emptyList(List<Foo> foo)
      {
         Assert.assertEquals(0, foo.size());
         return foo;
      }
   }

   @Path("/namespaced")
   public static class MyNamespacedResource
   {
      @Path("/array")
      @Produces("application/json;charset=UTF-8")
      @GET
      @Mapped(namespaceMap = @XmlNsMap(namespace = "http://foo.com", jsonName = "foo.com"))
      public NamespacedFoo[] get() throws Exception
      {
         NamespacedFoo[] foo = {new NamespacedFoo("bill{"), new NamespacedFoo("monica\"}")};
         return foo;
      }

      @Path("/array")
      @Produces("application/json")
      @Consumes("application/json")
      @POST

      public NamespacedFoo[] array(NamespacedFoo[] foo)
      {
         Assert.assertEquals(2, foo.length);
         Assert.assertEquals(foo[0].getTest(), "bill{");
         Assert.assertEquals(foo[1].getTest(), "monica\"}");
         return foo;
      }

      @Path("/list")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public List<NamespacedFoo> list(List<NamespacedFoo> foo)
      {
         Assert.assertEquals(2, foo.size());
         Assert.assertEquals(foo.get(0).getTest(), "bill{");
         Assert.assertEquals(foo.get(1).getTest(), "monica\"}");
         return foo;
      }


      @Path("/empty/array")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public NamespacedFoo[] emptyArray(NamespacedFoo[] foo)
      {
         Assert.assertEquals(0, foo.length);
         return foo;
      }

      @Path("/empty/list")
      @Produces("application/json")
      @Consumes("application/json")
      @POST
      public List<NamespacedFoo> emptyList(List<NamespacedFoo> foo)
      {
         Assert.assertEquals(0, foo.size());
         return foo;
      }
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(MyResource.class);
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
      dispatcher.getRegistry().addPerRequestResource(MyNamespacedResource.class);
      dispatcher.getRegistry().addPerRequestResource(MyResource2.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass()
   {
      client.close();
   }

   @Test
   public void testArray() throws Exception
   {
      Response response = client.target(generateURL("/array")).request().get();
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.readEntity(String.class));
      response.close();

      Builder request = client.target(generateURL("/array")).request();
      response = request.post(Entity.entity("[{\"foo\":{\"@test\":\"bill{\"}},{\"foo\":{\"@test\":\"monica\\\"}\"}}]", "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();

   }

   @Test
   public void testList() throws Exception
   {
      System.out.println("HERE");
      Response response = client.target(generateURL("/array")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);

      System.out.println("HERE 2");
      response = client.target(generateURL("/list")).request().post(Entity.entity(entity, "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();
      System.out.println("There");

   }

   @Test
   public void testNamespacedArray() throws Exception
   {
      System.out.println("Start");
      Response response = client.target(generateURL("/namespaced/array")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      response.close();

      response = client.target(generateURL("/namespaced/array")).request().post(Entity.entity(entity, "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();
      System.out.println("done");

   }

   @Test
   public void testNamespacedList() throws Exception
   {
      Response response = client.target(generateURL("/namespaced/array")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      response.close();

      response = client.target(generateURL("/namespaced/list")).request().post(Entity.entity(entity, "application/json"));
      Assert.assertEquals(200, response.getStatus());
      response.close();

   }

   @Test
   public void testEmptyArray() throws Exception
   {
      Response response = client.target(generateURL("/empty/array")).request().post(Entity.entity("[]", "application/json"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("[]", response.readEntity(String.class));
      response.close();

   }

   @Test
   public void testEmptyList() throws Exception
   {
      Response response = client.target(generateURL("/empty/list")).request().post(Entity.entity("[]", "application/json"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("[]", response.readEntity(String.class));
      response.close();

   }

   @Test
   public void testBadList() throws Exception
   {
      Response response = client.target(generateURL("/array")).request().post(Entity.entity("asdfasdfasdf", "application/json"));
      Assert.assertEquals(400, response.getStatus());
      response.close();

   }

   public static interface Store<T>
   {
      @GET
      @Path("/intf")
      @Produces("application/json")
      @BadgerFish
      @Wrapped
      public abstract List<T> list();

      @PUT
      @Path("/intf")
      @Consumes("application/json")
      public abstract void put(@Wrapped @BadgerFish List<T> list);
   }

   public static interface Accounts extends Store<Customer>
   {
   }

   @Path("/")
   public static class MyResource2 implements Accounts
   {
      public List<Customer> list()
      {
         ArrayList<Customer> set = new ArrayList<Customer>();
         set.add(new Customer("bill"));
         set.add(new Customer("monica"));

         return set;
      }

      public void put(List<Customer> customers)
      {
         Assert.assertEquals("bill", customers.get(0).getName());
         Assert.assertEquals("monica", customers.get(1).getName());
      }
   }

   public static class Parent<T>
   {
      public List<T> get()
      {
         return null;
      }
   }

   public static class Child extends Parent<Customer>
   {
   }

   /**
    * RESTEASY-167
    *
    * @throws Exception
    */
   @Test
   public void testIntfTempalte() throws Exception
   {
      Response response = client.target(generateURL("/intf")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String str = response.readEntity(String.class);
      System.out.println(str);
      response = client.target(generateURL("/intf")).request().put(Entity.entity(str, "application/json"));
      Assert.assertEquals(204, response.getStatus());  
      response.close();
   }


}