package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UrlExecutorTest extends BaseResourceTest
{
   @Path("/test")
   public static class TestService
   {
      @POST
      @Path("{name}")
      @Produces("text/plain")
      @Consumes("text/plain")
      public String post(@PathParam("name") String id, String message)
      {
         System.out.println("Server!!!");
         return message + id;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestService.class);
   }

   @Test
   public void testURLConnectionEngine() throws Exception
   {
      URLConnectionEngine engine = new URLConnectionEngine();
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/test/{name}"));
      Builder builder =  target.resolveTemplate("name", "Bill").request();
      ClientInvocation invocation = (ClientInvocation) builder.buildPost(Entity.entity("Hello ", "text/plain"));
      Response response = engine.invoke(invocation);
      String entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(entity, "Hello Bill");
      response = engine.invoke(invocation);
      entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(entity, "Hello Bill");
      builder = target.resolveTemplate("name", "Everyone").request();
      invocation = (ClientInvocation) builder.buildPost(Entity.entity("Goodbye ", "text/plain"));
      response = engine.invoke(invocation);
      entity = response.readEntity(String.class);
      System.out.println(entity);
      Assert.assertEquals(entity, "Goodbye Everyone");
   }
}
