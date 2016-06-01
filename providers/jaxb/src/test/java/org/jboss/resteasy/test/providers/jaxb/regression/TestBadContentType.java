package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for RESTEASY-169
 *
 * @author edelsonj
 */

public class TestBadContentType extends BaseResourceTest
{
   private static Client client;

   @Path("/test")
   public static class TestResource
   {

      @GET
      public TestBean get()
      {
         TestBean bean = new TestBean();
         bean.setName("myname");
         return bean;
      }

      @POST
      public void post(TestBean bean)
      {

      }

   }

   @XmlRootElement
   public static class TestBean
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
      addPerRequestResource(TestResource.class);
   }

   /**
    * RESTEASY-519
    *
    * @throws Exception
    */
   @Test
   public void testBadRequest() throws Exception
   {
      Builder request = client.target(generateURL("/test")).request();
      Response res = request.post(Entity.entity("<junk", "application/xml"));
      Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), res.getStatus());
   }

   /**
    *  * Test case for RESTEASY-169
    *
    * @throws Exception
    */
   @Test
   public void testHtmlError() throws Exception
   {
      Builder request = client.target(generateURL("/test")).request();
      Response response = request.accept("text/html").get();
      String entity = response.readEntity(String.class);
      System.out.println("response: " + entity);
      assertEquals(500, response.getStatus());
      assertTrue(entity.contains("media type: text/html"));
   }

}
