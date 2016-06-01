package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Jira575Test extends BaseResourceTest
{
   public static class Foo
   {

   }

   @Path("/regression")
   public static class RegressionResource
   {
      @GET
      public Response get()
      {
         return Response.status(401).entity("hello").type("application/error").build();
      }
   }

   @Path("/regression")
   public static interface RegressionProxy
   {
      @GET
      @Produces("application/foo")
      public Foo getFoo();
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(RegressionResource.class);
   }

   @Test
   public void testProxy() throws Exception
   {
      RegressionProxy proxy = TestPortProvider.createProxy(RegressionProxy.class, "/");
      try
      {
         proxy.getFoo();
      }
      catch (NotAuthorizedException e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 401);
         String val = (String)e.getResponse().readEntity(String.class);
         Assert.assertEquals("hello", val);

      }
   }

}
