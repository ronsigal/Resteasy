package org.jboss.resteasy.test.finegrain.client;

import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.smoke.SimpleResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseTest
{

   private static Dispatcher dispatcher;
   private static Client client;
   
   @Path("/")
   public interface ClientInterface
   {
      @GET
      @Path("basic")
      @Produces("text/plain")
      ClientResponse getBasic();

      @GET
      @Path("basic")
      @ClientResponseType(entityType = String.class)
      Response getBasicResponseString();

      @GET
      String getData(@ClientURI String uri);

      @PUT
      @Consumes("text/plain")
      Response.Status putData(@ClientURI URI uri, String data);

      @GET
      @Path("basic")
      ClientResponse getBasic2();

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      void putBasic(String body);

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      void putBasicInputStream(InputStream body);

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      Response.Status putBasicReturnCode(String body);

      @GET
      @Path("queryParam")
      @Produces("text/plain")
      ClientResponse getQueryParam(@QueryParam("param") String param);

      @GET
      @Path("uriParam/{param}")
      @Produces("text/plain")
      ClientResponse getUriParam(@PathParam("param") int param);

      @GET
      @Path("header")
      ClientResponse getHeaderClientResponse();

      @GET
      @Path("header")
      Response getHeaderResponse();

      @GET
      @Path("basic")
      ClientResponse getBasicBytes();

      @GET
      @Path("basic")
      @ClientResponseType(entityType = byte[].class)
      Response getBasicResponse();

      @GET
      @Path("error")
      ClientResponse getError();
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      client = ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Test
   public void testClientResponse() throws Exception
   {
      URI base = new URI(generateBaseUrl());
      testClient((ResteasyWebTarget) client.target(base));

      // uncomment this to test urlConnection executor. This has some hiccups
      // now

//       testClient(new ClientRequestFactory(new URLConnectionClientExecutor(), base));
   }

   private void testClient(ResteasyWebTarget target) throws URISyntaxException, Exception
   {

      ClientInterface client = target.proxy(ClientInterface.class);
      Assert.assertEquals("basic", client.getBasic().readEntity(String.class));
      Assert.assertEquals("basic", client.getBasicResponseString().readEntity(String.class));
      Assert.assertEquals("basic", client.getData(target.getUriBuilder().path("/basic").build().toString()));
      Assert.assertEquals("hello world", client.getQueryParam("hello world").readEntity(String.class));
      client.putBasic("hello world");

      client.putData(target.getUriBuilder().path("/basic").build(), "hello world2");
      Assert.assertEquals("hello world", client.getQueryParam("hello world").readEntity(String.class));
      
      String queryResult = target.path("/queryParam").queryParam("param", "hello world").request().get().readEntity(String.class);
      Assert.assertEquals("hello world", queryResult);

      Assert.assertTrue(1234 == client.getUriParam(1234).readEntity(int.class));

      Response paramPathResponse = target.path("uriParam/{param}").resolveTemplate("param", 1234).request().accept("text/plain").get();
      Assert.assertTrue(1234 == paramPathResponse.readEntity(int.class));
      paramPathResponse.close();

      Assert.assertEquals(Response.Status.NO_CONTENT, client.putBasicReturnCode("hello world"));
      Response putResponse = target.path("/basic").request().put(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), putResponse.getStatus());
      putResponse.close();

      Response crv = client.getHeaderClientResponse();
      Assert.assertEquals("headervalue", crv.getHeaderString("header"));
      crv.close();

      Response cr = target.path("/header").request().get();
      Assert.assertEquals("headervalue", cr.getHeaderString("header"));
      cr.close();
      
      cr = (ClientResponse) client.getHeaderResponse();
      Assert.assertEquals("headervalue", cr.getMetadata().getFirst("header"));
      cr.close();
      
      Assert.assertTrue(Arrays.equals("basic".getBytes(), client.getBasicBytes().readEntity(byte[].class)));
      Assert.assertTrue(Arrays.equals("basic".getBytes(), client.getBasicResponse().readEntity(byte[].class)));

      Assert.assertTrue(Arrays.equals("basic".getBytes(), target.path("/basic").request().get().readEntity(byte[].class)));

      Assert.assertEquals("basic", client.getBasic2().readEntity(String.class));

      ClientResponse basicResponse = (ClientResponse) target.path("/basic").request().get();
      Assert.assertEquals("basic", basicResponse.readEntity(String.class));
      basicResponse.close();
   }

   @Test
   public void testErrorResponse() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL("/shite"));
      ClientInterface proxy = target.proxy(ClientInterface.class);
      ClientResponse response = proxy.getBasic();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      response.releaseConnection();
      response = proxy.getError();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      response.close();
   }

   @Path("/redirect")
   public static class RedirectResource
   {
      @GET
      public Response get()
      {
         try
         {
            return Response.seeOther(createURI("/redirect/data")).build();
         }
         catch (IllegalArgumentException e)
         {
            throw new RuntimeException(e);
         }
      }

      @GET
      @Path("data")
      public String getData()
      {
         return "data";
      }
   }

   @Path("/redirect")
   public static interface RedirectClient
   {
      @GET
      ClientResponse get();
   }

   @Test
   public void testRedirect() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(RedirectResource.class);
      {
         ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
         RedirectClient proxy = target.proxy(RedirectClient.class);
         testRedirect(proxy.get());
         testRedirect((ClientResponse) target.path("/redirect").request().get());
      }
      System.out.println("*****");
      {
         URL url = createURL("/redirect");
         // HttpURLConnection.setFollowRedirects(false);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setInstanceFollowRedirects(false);
         conn.setRequestMethod("GET");
         for (Object name : conn.getHeaderFields().keySet())
         {
            System.out.println(name);
         }
         System.out.println(conn.getResponseCode());
      }

   }

   private void testRedirect(ClientResponse response)
   {
      MultivaluedMap<String, ?> headers = response.getHeaders();
      System.out.println("size: " + headers.size());
      for (Object name : headers.keySet())
      {
         System.out.println(name + ":" + headers.getFirst(name.toString()));
      }
      Assert.assertEquals((String) headers.getFirst("location"), generateURL("/redirect/data"));
      response.close();
   }

}