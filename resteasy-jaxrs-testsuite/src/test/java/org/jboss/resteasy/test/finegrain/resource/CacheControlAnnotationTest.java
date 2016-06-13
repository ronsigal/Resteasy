package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheControlAnnotationTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource
   {
      @GET
      @Cache(maxAge = 3600)
      @Path("/maxage")
      public String getMaxAge()
      {
         return "maxage";
      }

      @GET
      @NoCache
      @Path("nocache")
      public String getNoCache()
      {
         return "nocache";
      }

   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Resource.class);
   }

   @Test
   public void testResource() throws Exception
   {
      Builder builder = ClientBuilder.newClient().target(generateURL("/maxage")).request();
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         System.out.println("Cache-Control: " + response.getHeaderString("cache-control"));
         CacheControl cc = CacheControl.valueOf(response.getHeaderString("cache-control"));
         Assert.assertFalse(cc.isPrivate());
         Assert.assertEquals(3600, cc.getMaxAge());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testResource2() throws Exception
   {
      Builder builder = ClientBuilder.newClient().target(generateURL("/nocache")).request();
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         String value = response.getHeaderString("cache-control");
         Assert.assertEquals("no-cache", value);
         System.out.println("Cache-Control: " + value);
         CacheControl cc = CacheControl.valueOf(value);
         Assert.assertTrue(cc.isNoCache());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

}
