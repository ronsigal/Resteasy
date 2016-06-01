package org.jboss.resteasy.test.resteasy767;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy767.TestApplication;
import org.jboss.resteasy.resteasy767.TestMessageBodyWriterInterceptor;
import org.jboss.resteasy.resteasy767.TestPostProcessFilter;
import org.jboss.resteasy.resteasy767.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2013
 */
@RunWith(Arquillian.class)
public class AsyncPostProcessingTest
{
   private static Client client;
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-767.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestMessageBodyWriterInterceptor.class, TestPostProcessFilter.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

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
   public void testSync() throws Exception
   {
      reset();
      Builder request = client.target("http://localhost:9090/RESTEASY-767/sync").request();
      Response response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessFilter.called);
      String entity = response.readEntity(String.class);
      System.out.println("returned entity: " + entity);
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessFilter.called);
      Assert.assertEquals("sync", entity);
   }
   
   @Test
   public void testAsyncWithDelay() throws Exception
   {
      reset();
      Builder request = client.target("http://localhost:9090/RESTEASY-767/async/delay").request();
      Response response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessFilter.called);
      String entity = response.readEntity(String.class);
      System.out.println("returned entity: " + entity);
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessFilter.called);
      Assert.assertEquals("async/delay", entity);
   }
   
   @Test
   public void testAsyncWithNoDelay() throws Exception
   {
      reset();
      Builder request = client.target("http://localhost:9090/RESTEASY-767/async/nodelay").request();
      Response response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessFilter.called);
      String entity = response.readEntity(String.class);
      System.out.println("returned entity: " + entity);
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessFilter.called);
      Assert.assertEquals("async/nodelay", entity);
   }
   
   private void reset()
   {
      TestMessageBodyWriterInterceptor.called = false;
      TestPostProcessFilter.called = false;
   }
}
