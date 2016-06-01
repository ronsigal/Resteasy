package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.DateUtil;
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
import java.util.Date;
import java.util.Hashtable;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionRfc7232Test
{

   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<>();
      initParams.put("resteasy.rfc7232preconditions", "true");

      dispatcher = EmbeddedContainer.start(initParams).getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(PrecedenceResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
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
