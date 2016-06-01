package org.jboss.resteasy.test.finegrain.resource;

import java.util.Date;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.GregorianCalendar;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.jboss.resteasy.util.DateUtil;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionTest
{

   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(LastModifiedResource.class);
      dispatcher.getRegistry().addPerRequestResource(EtagResource.class);
      dispatcher.getRegistry().addPerRequestResource(PrecedenceResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class LastModifiedResource
   {

      @GET
      public Response doGet(@Context Request request)
      {
         GregorianCalendar lastModified = new GregorianCalendar(2007, 0, 0, 0, 0, 0);
         Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified.getTime());
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(412, response.getStatus());
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
   public void testIfUnmodifiedSinceAfterLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
   public void testIfModifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
   public void testIfModifiedSinceAfterLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
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
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(412, response.getStatus());
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
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceAfterLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
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
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceAfterLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
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
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(200, response.getStatus());
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

   @Path("/etag")
   public static class EtagResource
   {

      @GET
      public Response doGet(@Context Request request)
      {
         Response.ResponseBuilder rb = request.evaluatePreconditions(new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }

      @Context
      Request myRequest;

      @GET
      @Path("/fromField")
      public Response doGet()
      {
         Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }

       @GET
       @Path("/weak")
       public Response GetWeak() {
           Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1", true));
           if (rb != null)
               return rb.build();

           return Response.ok("foo", "text/plain").build();
       }

   }

   @Test
   public void testIfMatchWithMatchingETag()
   {
      testIfMatchWithMatchingETag("");
      testIfMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag()
   {
      testIfMatchWithoutMatchingETag("");
      testIfMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWildCard()
   {
      testIfMatchWildCard("");
      testIfMatchWildCard("/fromField");
   }

   @Test
   public void testIfNonMatchWithMatchingETag()
   {
      testIfNonMatchWithMatchingETag("");
      testIfNonMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfNonMatchWithoutMatchingETag()
   {
      testIfNonMatchWithoutMatchingETag("");
      testIfNonMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfNonMatchWildCard()
   {
      testIfNonMatchWildCard("");
      testIfNonMatchWildCard("/fromField");
   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag()
   {
      testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("");
      testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("/fromField");

   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("");
      testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag()
   {
      testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("");
      testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("");
      testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithMatchingWeakETag()
   {
      Builder builder = client.target(generateURL("/etag/weak")).request();
      builder.header(HttpHeaderNames.IF_MATCH, "W/\"1\"");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
   public void testIfMatchWithNonMatchingWeakEtag()
   {
      Builder builder = client.target(generateURL("/etag/weak")).request();
      builder.header(HttpHeaderNames.IF_MATCH, "W/\"2\"");
      Response response = null;
      try
      {
          response = builder.get();
          Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
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

   ////////////

   public void testIfMatchWithMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "\"1\"");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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

   public void testIfMatchWithoutMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "\"2\"");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(412, response.getStatus());
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

   public void testIfMatchWildCard(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "*");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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

   public void testIfNonMatchWithMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "\"1\"");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getHeaderString(HttpHeaderNames.ETAG));
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

   public void testIfNonMatchWithoutMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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

   public void testIfNonMatchWildCard(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "*");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getHeaderString(HttpHeaderNames.ETAG));
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

   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "1");
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "1");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getHeaderString(HttpHeaderNames.ETAG));
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

   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "1");
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(200, response.getStatus());
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

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "2");
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "1");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(412, response.getStatus());
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

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      Builder builder = client.target(generateURL("/etag" + fromField)).request();
      builder.header(HttpHeaderNames.IF_MATCH, "2");
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(412, response.getStatus());
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

   @Path("/precedence")
   public static class PrecedenceResource
   {
      @GET
      public Response doGet(@Context Request request)
      {
         Date lastModified = DateUtil.parseDate("Mon, 1 Jan 2007 00:00:00 GMT");
         Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified, new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testPrecedence_AllMatch()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_MATCH, "1");  // true
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
   public void testPrecedence_IfMatchWithNonMatchingEtag()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_MATCH, "2");  // false
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
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
   public void testPrecedence_IfMatchNotPresentUnmodifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); //false
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
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
   public void testPrecedence_IfNoneMatchWithMatchingEtag()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "1");  // true
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
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
   public void testPrecedence_IfNoneMatchWithNonMatchingEtag()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // false
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
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
   public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceBeforeLastModified()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // false
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
   public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceAfterLastModified()
   {
      Builder builder = client.target(generateURL("/precedence")).request();
      builder.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");  // true
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
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
