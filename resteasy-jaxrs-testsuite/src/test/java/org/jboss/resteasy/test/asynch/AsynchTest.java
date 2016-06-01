package org.jboss.resteasy.test.asynch;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchTest
{
   private static CountDownLatch latch;

   private static AsynchronousDispatcher dispatcher;

   @Path("/")
   public static class MyResource
   {
      @Context
      private ServletConfig config;

      @Context
      private ServletContext context;


      @POST
      public String post(String content) throws Exception
      {
         System.out.println("in post");
         Assert.assertNotNull(config);
         Assert.assertNotNull(context);
         System.out.println("Asserts passed");
         config.getServletContext();
         context.getMajorVersion();
         System.out.println("Called injected passed");

         Thread.sleep(1500);
         latch.countDown();

         return content;
      }

      @PUT
      public void put(String content) throws Exception
      {
         System.out.println("IN PUT!!!!");
         Assert.assertNotNull(config);
         Assert.assertNotNull(context);
         System.out.println("Asserts passed");
         config.getServletContext();
         context.getMajorVersion();
         System.out.println("Called injected passed");
         Assert.assertEquals("content", content);
         Thread.sleep(500);
         System.out.println("******* countdown ****");
         latch.countDown();
         System.out.println("******* countdown complete ****");
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setAsyncJobServiceEnabled(true);
      EmbeddedContainer.start(deployment);

      dispatcher = (AsynchronousDispatcher) deployment.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      Thread.sleep(1000);
   }

   @Test
   public void testOneway() throws Exception
   {
      Builder builder = ResteasyClientBuilder.newClient().target(generateURL("?oneway=true")).request();
      Response response = null;
      try
      {
         latch = new CountDownLatch(1);
         long start = System.currentTimeMillis();
         response = builder.put(Entity.entity("content", "text/plain"));
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         Assert.assertTrue(end < 1000);
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testAsynch() throws Exception
   {
      Client client = ResteasyClientBuilder.newClient();
      Builder builder = null;
      Response response = null;
      
      {
         latch = new CountDownLatch(1);
         builder = client.target(generateURL("?asynch=true")).request();
         long start = System.currentTimeMillis();
         response = builder.post(Entity.entity("content", "text/plain"));
         @SuppressWarnings("unused")
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
         System.out.println("JOB: " + jobUrl);
         response.close();

         builder = client.target(jobUrl).request();
         response = builder.get();
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.close();
         // there's a lag between when the latch completes and the executor
         // registers the completion of the call 
         URI uri = new URI(jobUrl);
         String query = (uri.getQuery() == null ? "" : "&") + "wait=1000";
         URI newURI = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
         builder = client.target(newURI.toString()).request();
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.readEntity(String.class), "content");

         // test its still there
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.readEntity(String.class), "content");

         // delete and test delete
         builder = client.target(jobUrl).request();
         response = builder.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.close();
      }
      
      // test cache size
      {
         dispatcher.setMaxCacheSize(1);
         latch = new CountDownLatch(1);
         builder = client.target(generateURL("?asynch=true")).request();
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl1 = response.getHeaderString(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.close();
         
         latch = new CountDownLatch(1);
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         Assert.assertTrue(!jobUrl1.equals(jobUrl2));
         response.close();

         builder = client.target(jobUrl1).request();
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.close();

         // test its still there
         builder = client.target(jobUrl2).request();
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.readEntity(String.class), "content");

         // delete and test delete
         builder = client.target(jobUrl2).request();
         response = builder.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.close();
      }
      
      // test readAndRemove
      {
         dispatcher.setMaxCacheSize(10);
         latch = new CountDownLatch(1);
         builder = client.target(generateURL("?asynch=true")).request();
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.close();
  
         // test its still there
         builder = client.target(jobUrl2).request();
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus()) ;
         Assert.assertEquals(response.readEntity(String.class), "content");  
         response.close();

         builder = client.target(jobUrl2).request();
         response = builder.get();
         Thread.sleep(1000);
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.close();
      }
   }

}
