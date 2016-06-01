package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatchedResourceTest
{
   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/test1/{id}.xml.{lang}")
      @GET
      public String getComplex()
      {
         return "complex";
      }

      @Path("/test1/{id}")
      @GET
      public String getSimple()
      {
         return "simple";
      }

      @Path("/test2/{id}")
      @GET
      public String getSimple2()
      {
         return "simple2";
      }

      @Path("/test2/{id}.xml.{lang}")
      @GET
      public String getComplex2()
      {
         return "complex2";
      }

      @Path("match")
      @Produces("*/*;qs=0.0")
      @GET
      public String getObj()
      {
         return "*/*";
      }

      @Path("match")
      @Produces("application/xml")
      @GET
      public String getObjXml()
      {
         return "<xml/>";
      }

      @Path("match")
      @Produces("application/json")
      @GET
      public String getObjJson()
      {
         return "{ \"name\" : \"bill\" }";
      }

      @Path("start")
      @POST
      @Produces("text/plain")
      public String start()
      {
         return "started";
      }

      @Path("start")
      @Consumes("application/xml")
      @POST
      @Produces("text/plain")
      public String start(String xml)
      {
         return xml;
      }

   }

   /**
    * RESTEASY-549
    *
    * @throws Exception
    */
   @Test
   public void testEmpty() throws Exception
   {
      WebTarget target = client.target(generateURL("/start"));
      String rtn = target.request().post(null, String.class);
      Assert.assertEquals("started", rtn);

      rtn = target.request().post(Entity.entity("<xml/>", "application/xml"), String.class);
      Assert.assertEquals("<xml/>", rtn);

   }

   /**
    * RESTEASY-537
    *
    * @throws Exception
    */
   @Test
   public void testMatch() throws Exception
   {
      Builder builder = client.target(generateURL("/match")).request();
      builder.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
      Response rtn = builder.get();
      Assert.assertEquals("text/html", rtn.getHeaderString("Content-Type"));
      String res = rtn.readEntity(String.class);
      Assert.assertEquals("*/*", res);
   }

   public void _test(String uri, String value)
   {
      try
      {
         Response response = client.target(uri).request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals(value, response.readEntity(String.class));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPost()
   {
      _test(generateURL("/test1/foo.xml.en"), "complex");
      _test(generateURL("/test2/foo.xml.en"), "complex2");
   }

}