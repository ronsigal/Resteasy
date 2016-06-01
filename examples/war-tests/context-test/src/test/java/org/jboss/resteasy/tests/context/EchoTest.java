package org.jboss.resteasy.tests.context;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-184
 * 
 * NOTE. Interceptors are no longer prioritized as in this test, with Resteasy
 *       specific priorities. However, the test can still be made to pass with
 *       the JAX-RS 2.0 @Priority annotation.
 */
public class EchoTest
{
   private static Client client;
   
   @BeforeClass
   public static void beforeClass()
   {
      client = ResteasyClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }
   
   @Test
   public void testForward() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/forward").request().get();
      Assert.assertEquals("hello world", response.readEntity(String.class));
   }

   @Test
   public void testRepeat() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/test").request().get();
      Assert.assertEquals("http://localhost:9095/test/", response.readEntity(String.class));
   }

   @Test
   public void testEmpty() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/").request().get();
      Assert.assertEquals("http://localhost:9095/test/", response.readEntity(String.class));
   }

   @Test
   public void testServletContext() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/test/servletcontext").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", response.readEntity(String.class));
      Assert.assertTrue(response.getHeaders().containsKey("before-encoder"));
      Assert.assertTrue(response.getHeaders().containsKey("after-encoder"));
      Assert.assertTrue(response.getHeaders().containsKey("end"));
      Assert.assertTrue(response.getHeaders().containsKey("encoder"));
   }

   @Test
   public void testServletConfig() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/test/servletconfig").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", response.readEntity(String.class));
   }

   @Test
   public void testXmlMappings() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/stuff.xml").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("xml", response.readEntity(String.class));

   }

   @Test
   public void testJsonMappings() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      Response response = client.target("http://localhost:9095/test/stuff.json").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("json", response.readEntity(String.class));

   }
}

