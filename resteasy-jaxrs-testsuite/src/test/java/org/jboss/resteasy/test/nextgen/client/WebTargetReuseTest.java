package org.jboss.resteasy.test.nextgen.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WebTargetReuseTest extends BaseResourceTest
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
   public void testTargetReuse() throws Exception
   {
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/test/{name}"));
      Builder builder = target.resolveTemplate("name", "Bill").request();
      Response response = builder.post(Entity.entity("Hello ", "text/plain"));
      String result = response.readEntity(String.class);
      System.out.println(result);
      Assert.assertEquals(result, "Hello Bill");
      
      response = builder.post(Entity.entity("Hello ", "text/plain"));
      result = response.readEntity(String.class);
      System.out.println(result);
      Assert.assertEquals(result, "Hello Bill");
      
      builder = target.resolveTemplate("name", "Everyone").request();
      response = builder.post(Entity.entity("Goodbye ", "text/plain"));
      result = response.readEntity(String.class);
      System.out.println(result);
      Assert.assertEquals(result, "Goodbye Everyone");
   }
}
