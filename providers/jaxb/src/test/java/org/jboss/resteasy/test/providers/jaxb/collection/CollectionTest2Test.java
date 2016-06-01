package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CollectionTest2Test extends BaseResourceTest
{
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
      @Produces("application/xml")
      @Consumes("application/xml")
      @POST
      public Foo[] naked(Foo[] foo)
      {
         Assert.assertEquals(1, foo.length);
         Assert.assertEquals(foo[0].getTest(), "hello");
         return foo;
      }

      @Path("/list")
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Wrapped(element = "list", namespace = "", prefix = "")
      public List<Foo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<Foo> list)
      {
         Assert.assertEquals(1, list.size());
         Assert.assertEquals(list.get(0).getTest(), "hello");
         return list;
      }


   }

   @Path("/namespaced")
   public static class MyNamespacedResource
   {
      @Path("/array")
      @Produces("application/xml")
      @Consumes("application/xml")
      @POST
      public NamespacedFoo[] naked(NamespacedFoo[] foo)
      {
         Assert.assertEquals(1, foo.length);
         Assert.assertEquals(foo[0].getTest(), "hello");
         return foo;
      }

      @Path("/list")
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Wrapped(element = "list", namespace = "", prefix = "")
      public List<NamespacedFoo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<NamespacedFoo> list)
      {
         Assert.assertEquals(1, list.size());
         Assert.assertEquals(list.get(0).getTest(), "hello");
         return list;
      }


   }

   @Before
   public void setup()
   {
      addPerRequestResource(MyResource.class);
      addPerRequestResource(MyNamespacedResource.class);
   }

   @Test
   public void testNakedArray() throws Exception
   {
      String xml = "<resteasy:collection xmlns:resteasy=\"http://jboss.org/resteasy\">"
              + "<foo test=\"hello\"/></resteasy:collection>";

      Builder request = ClientBuilder.newClient().target(generateURL("/array")).request();
      List<Foo> list= request.post(Entity.entity(xml, "application/xml"), new GenericType<List<Foo>>() {});
      Assert.assertEquals(1, list.size());
      Assert.assertEquals(list.get(0).getTest(), "hello");

   }

   @Test
   public void testList() throws Exception
   {
      String xml = "<list>"
              + "<foo test=\"hello\"/></list>";

      Builder request = ClientBuilder.newClient().target(generateURL("/list")).request();
      Foo[] list= request.post(Entity.entity(xml, "application/xml"), new GenericType<Foo[]>() {});
      Assert.assertEquals(1, list.length);
      Assert.assertEquals(list[0].getTest(), "hello");

   }

   @Test
   public void testNamespacedNakedArray() throws Exception
   {
      String xml = "<collection xmlns:foo=\"http://foo.com\">"
              + "<foo:foo test=\"hello\"/></collection>";

      Builder request = ClientBuilder.newClient().target(generateURL("/namespaced/array")).request();
      List<NamespacedFoo> list= request.post(Entity.entity(xml, "application/xml"), new GenericType<List<NamespacedFoo>>() {});
      Assert.assertEquals(1, list.size());
      Assert.assertEquals(list.get(0).getTest(), "hello");

   }

   @Test
   public void testNamespacedList() throws Exception
   {
      String xml = "<list xmlns:foo=\"http://foo.com\">"
              + "<foo:foo test=\"hello\"/></list>";

      Builder request = ClientBuilder.newClient().target(generateURL("/namespaced/list")).request();
      NamespacedFoo[] list= request.post(Entity.entity(xml, "application/xml"), new GenericType<NamespacedFoo[]>() {});
      Assert.assertEquals(1, list.length);
      Assert.assertEquals(list[0].getTest(), "hello");

   }

   @Test
   public void testBadList() throws Exception
   {
      String xml = "<bad-list>"
              + "<foo test=\"hello\"/></bad-list>";

      Builder request = ClientBuilder.newClient().target(generateURL("/list")).request();
      Response response = request.post(Entity.entity(xml, "application/xml"));
      Assert.assertEquals(400, response.getStatus());

   }

}
