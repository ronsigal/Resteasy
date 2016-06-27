package org.jboss.resteasy.test.finegrain.client;

import org.junit.Assert;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ResponseObjectTest extends BaseResourceTest
{
   @Path("test")
   static interface ResponseObjectClient
   {
      @GET
      BasicObject get();

      @GET
      @Path("link-header")
      HateoasObject performGetBasedOnHeader();
   }

   @ResponseObject
   public static interface BasicObject
   {
      @Status
      int status();

      @Body
      String body();

      Response response();

      @HeaderParam("Content-Type")
      String contentType();
   }

   @ResponseObject
   public static interface HateoasObject
   {
      @Status
      int status();

      @LinkHeaderParam(rel = "nextLink")
      URI nextLink();

      @GET
      @LinkHeaderParam(rel = "nextLink")
      String followNextLink();
   }

   @Path("test")
   public static class ResponseObjectResource
   {

      @GET
      @Produces("text/plain")
      public String get()
      {
         return "ABC";
      }

      @GET
      @Path("/link-header")
      public Response getWithHeader(@Context UriInfo uri)
      {
         URI subUri = uri.getAbsolutePathBuilder().path("next-link").build();
         Link link = new LinkBuilderImpl().uri(subUri).rel("nextLink").build();
         return Response.noContent().header("Link", link.toString()).build();
      }

      @GET
      @Produces("text/plain")
      @Path("/link-header/next-link")
      public String getHeaderForward()
      {
         return "forwarded";
      }
   }

   private static ResponseObjectClient client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(ResponseObjectResource.class);
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(TestPortProvider.generateBaseUrl());
      client = target.proxy(ResponseObjectClient.class);
   }

   @Test
   public void testSimple()
   {
      BasicObject obj = client.get();
      Assert.assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), obj.status());
      Assert.assertEquals("ABC", obj.body());
      Assert.assertEquals("text/plain", obj.response().getHeaderString("Content-Type"));
      Assert.assertEquals("text/plain", obj.contentType());
   }

   @Test
   public void testLinkFollow()
   {
      HateoasObject obj = client.performGetBasedOnHeader();
      Assert.assertEquals(javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode(), obj.status());
      Assert.assertTrue(obj.nextLink().getPath().endsWith("next-link"));
      Assert.assertEquals("forwarded", obj.followNextLink());
   }
}
