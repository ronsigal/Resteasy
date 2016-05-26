package org.jboss.resteasy.test.nextgen.interceptors;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1266
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class FilteredCookieTest
{
   private static final Logger log = Logger.getLogger(FilteredCookieTest.class);
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("test")
   public static class TestResource
   {
      private @Context HttpHeaders headers;
      
      @GET
      @Path("get")
      public Response getCookie()
      {
         NewCookie cookie = new NewCookie(OLD_COOKIE_NAME, "value");
         return Response.ok().cookie(cookie).build();
      }
      
      @GET
      @Path("return")
      public Response returnCookie()
      {
         Cookie oldCookie = headers.getCookies().get(OLD_COOKIE_NAME);
         Cookie newCookie = headers.getCookies().get(NEW_COOKIE_NAME);
         log.info("returnCookie(): cookies: " + oldCookie + " / " + newCookie);
         ResponseBuilder builder = Response.ok();
         builder.cookie(new NewCookie(oldCookie.getName(), oldCookie.getValue()));
         builder.cookie(new NewCookie(newCookie.getName(), newCookie.getValue()));
         return builder.build();
      }
   }

   @Provider
   @PreMatching
   public static class TestFilter implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         final Cookie cookie = requestContext.getCookies().get(OLD_COOKIE_NAME);
         log.info("TestFilter.filter(): cookie: " + cookie);
         if (cookie != null)
         {
            requestContext.getHeaders().add(HttpHeaders.COOKIE, new Cookie(NEW_COOKIE_NAME, cookie.getValue()).toString());
         }
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().register(TestFilter.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Test
   public void test()
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8081/test/get");
      Response response = target.request().get();
      log.info("status: " + response.getStatus());
      NewCookie cookie = response.getCookies().get(OLD_COOKIE_NAME);
      log.info("set-cookie 1: " + cookie);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertNotNull(cookie);
      client.close();
      
      client = ClientBuilder.newClient();
      target = client.target("http://localhost:8081/test/return");
      Builder builder = target.request();
      response = builder.cookie(cookie).get();
      log.info("status: " + response.getStatus());
      NewCookie oldCookie = response.getCookies().get(OLD_COOKIE_NAME);
      NewCookie newCookie = response.getCookies().get(NEW_COOKIE_NAME);
      log.info("set-cookies 2: " + oldCookie + " / " + newCookie);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertNotNull(oldCookie);
      Assert.assertNotNull(newCookie);
      client.close();
   }
}
