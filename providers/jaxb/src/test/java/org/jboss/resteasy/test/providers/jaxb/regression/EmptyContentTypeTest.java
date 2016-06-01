package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * RESTEASY-518, 529
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmptyContentTypeTest  extends BaseResourceTest
{
   private static Client client;
   
   @XmlRootElement
   public static class Foo
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

   }

   @Path("/test1")
   public static class Test1
   {
      @POST
      @Consumes(MediaType.APPLICATION_XML)
      public Response post(Foo foo)
      {
         return Response.ok(foo.getName(), "text/plain").build();
      }


      @POST
      public Response postNada(@HeaderParam("Content-Type") String contentType)
      {
         Assert.assertEquals(null, contentType);
         return Response.ok("NULL", "text/plain").build();
      }
   }


   @Path("/test2")
   public static class Test2
   {
      @POST
      public Response postNada(@HeaderParam("Content-Type") String contentType)
      {
         Assert.assertEquals(null, contentType);
         return Response.ok("NULL", "text/plain").build();
      }

      @POST
      @Consumes(MediaType.APPLICATION_XML)
      public Response post(Foo foo)
      {
         return Response.ok(foo.getName(), "text/plain").build();
      }


   }

   @BeforeClass
   public static void beforeClass()
   {
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }
   
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Test1.class);
      addPerRequestResource(Test2.class);
   }

   @Test
   public void test1() throws Exception
   {
      Builder request = client.target(generateURL("/test1")).request();
      Foo foo = new Foo();
      foo.setName("Bill");
      Response res = request.post(Entity.entity(foo, "application/xml"));
      Assert.assertEquals(res.readEntity(String.class), "Bill");

      request = client.target(generateURL("/test1")).request();
      res = request.post(null);
      Assert.assertEquals(res.readEntity(String.class), "NULL");


   }

   @Test
   public void test2() throws Exception
   {
      Builder request = client.target(generateURL("/test2")).request();
      Foo foo = new Foo();
      foo.setName("Bill");
      Response res = request.post(Entity.entity(foo, "application/xml"));
      Assert.assertEquals(res.readEntity(String.class), "Bill");

      request = client.target(generateURL("/test2")).request();
      res = request.post(null);
      Assert.assertEquals(res.readEntity(String.class), "NULL");


   }

}
