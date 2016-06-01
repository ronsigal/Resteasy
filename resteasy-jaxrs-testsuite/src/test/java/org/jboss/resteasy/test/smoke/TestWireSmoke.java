package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireSmoke
{

   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      {
         Response response = client.target(generateURL("/basic")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/basic")).request().put(Entity.entity("basic", "text/plain"));
         Assert.assertEquals(204, response.getStatus());
         response.close();
      }
      
      {
         Response response = client.target(generateURL("/queryParam")).queryParam("param", "hello world").request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/uriParam/1234")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.readEntity(String.class));
      }

      dispatcher.getRegistry().removeRegistrations(SimpleResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(LocatingResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      {
         Response response = client.target(generateURL("/locating/basic")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/locating/basic")).request().put(Entity.entity("basic", "text/plain"));
         Assert.assertEquals(204, response.getStatus());
         response.close();
      }

      {
         Response response = client.target(generateURL("/locating/queryParam")).queryParam("param", "hello world").request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/locating/uriParam/1234")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.readEntity(String.class));
      }

      dispatcher.getRegistry().removeRegistrations(LocatingResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }
}