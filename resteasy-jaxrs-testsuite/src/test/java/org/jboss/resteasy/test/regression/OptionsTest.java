package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OptionsTest extends BaseResourceTest
{
   private static Client client;
   
   @Path("params")
   public static class ParamsResource
   {
      @Path("/customers/{custid}/phonenumbers")
      @GET
      @Produces("text/plain")
      public String getPhoneNumbers()
      {
         return "912-111-1111";
      }

      @Path("/customers/{custid}/phonenumbers/{id}")
      @GET
      @Produces("text/plain")
      public String getPhoneIds()
      {
         return "1111";
      }
   }

   // RESTEASY-363

   @Path("/users")
   public static class Users
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "users";
      }

      @POST
      @Consumes("text/plain")
      public void post(String users)
      {

      }

      @GET
      @Path("{user-id}")
      @Produces("text/plain")
      public String getUserId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @DELETE
      @Path("{user-id}")
      @Produces("text/plain")
      public String deleteUserId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @PUT
      @Path("{user-id}")
      @Consumes("text/plain")
      public void postUserId(@PathParam("user-id") String userId, String user)
      {

      }

      @GET
      @Path("{user-id}/contacts")
      @Produces("text/plain")
      public String getContacts(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @POST
      @Path("{user-id}/contacts")
      @Consumes("text/plain")
      public void postContacts(@PathParam("user-id") String userId, String user)
      {

      }

      @GET
      @Path("{user-id}/contacts/{contact-id}")
      @Produces("text/plain")
      public String getContactId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @DELETE
      @Path("{user-id}/contacts/{contact-id}")
      @Produces("text/plain")
      public String deleteCotactId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @PUT
      @Path("{user-id}/contacts/{contact-id}")
      @Consumes("text/plain")
      public void postContactId(@PathParam("user-id") String userId, String user)
      {

      }

   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(ParamsResource.class);
      addPerRequestResource(Users.class);
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }

   @Test
   public void testOptions() throws Exception
   {
      Response response = client.target(TestPortProvider.generateURL("/params/customers/333/phonenumbers")).request().options();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   @Test
   public void testMethodNotAllowed() throws Exception
   {
      Response response = client.target(TestPortProvider.generateURL("/params/customers/333/phonenumbers")).request().post(null);
      Assert.assertEquals(405, response.getStatus());
      response.close();

      // RESTEasy-363

      response = client.target(TestPortProvider.generateURL("/users")).request().delete();
      Assert.assertEquals(405, response.getStatus());
      response.close();

      response = client.target(TestPortProvider.generateURL("/users/53")).request().post(null);
      Assert.assertEquals(405, response.getStatus());
      response.close();

      response = client.target(TestPortProvider.generateURL("/users/53/contacts")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();

      response = client.target(TestPortProvider.generateURL("/users/53/contacts")).request().delete();
      Assert.assertEquals(405, response.getStatus());
      response.close();

      response = client.target(TestPortProvider.generateURL("/users/53/contacts/carl")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();

      response = client.target(TestPortProvider.generateURL("/users/53/contacts/carl")).request().post(null);
      Assert.assertEquals(405, response.getStatus());
      response.close();


   }
}
