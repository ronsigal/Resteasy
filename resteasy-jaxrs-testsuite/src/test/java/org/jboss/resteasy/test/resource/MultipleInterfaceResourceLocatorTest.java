package org.jboss.resteasy.test.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class MultipleInterfaceResourceLocatorTest
{
   protected ResteasyDeployment deployment;

   public interface Intf1
   {
      @GET
      @Produces("text/plain")
      @Path("hello1")
      public String resourceMethod1();
   }

   public interface Intf2
   {
      @GET
      @Produces("text/plain")
      @Path("hello2")
      public String resourceMethod2();
   }

   @Path("")
   static public class TestSubresource implements Intf1, Intf2
   {
      @Override
      public String resourceMethod1()
      {
         return "resourceMethod1";
      }

      @Override
      public String resourceMethod2()
      {
         return "resourceMethod2";
      }
   }

   @Path("/")
   static public class TestResource
   {
      @Produces("text/plain")
      @Path("test")
      public Object resourceLocator()
      {
         return new TestSubresource();
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void test() throws Exception
   {
      WebTarget target = ClientBuilder.newClient().target(generateURL("/test"));
      Response response = target.path("/hello1/").request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received first response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("resourceMethod1", entity);

      response = target.path("/hello2/").request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received second response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("resourceMethod2", entity);
   }
}
