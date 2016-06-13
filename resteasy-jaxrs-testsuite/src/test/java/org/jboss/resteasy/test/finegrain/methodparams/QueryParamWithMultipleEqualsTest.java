package org.jboss.resteasy.test.finegrain.methodparams;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class QueryParamWithMultipleEqualsTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("/")
   public static class TestResource
   {
      @Path("test")
      @GET
      public String test(@QueryParam("foo") String incoming)
      {
         return incoming;
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
	   dispatcher = EmbeddedContainer.start().getDispatcher();
	   dispatcher.getRegistry().addPerRequestResource(TestResource.class);;
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testQueryParam() throws Exception
   {
      Builder builder = ResteasyClientBuilder.newClient().target(generateURL("/test?foo=weird=but=valid")).request();
      Response response = builder.get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertEquals(entity, "weird=but=valid");
      after();
   }
}
