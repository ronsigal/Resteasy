package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashSet;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MethodDefaultTest extends BaseResourceTest
{
   private static Client client;

   @Path(value = "/GetTest")
   public static class Resource
   {

      static String html_content =
              "<html>" + "<head><title>CTS-get text/html</title></head>" +
                      "<body>CTS-get text/html</body></html>";

      @GET
      public Response getPlain()
      {
         return Response.ok("CTS-get text/plain").header("CTS-HEAD", "text-plain").
                 build();
      }

      @GET
      @Produces(value = "text/html")
      public Response getHtml()
      {
         return Response.ok(html_content).header("CTS-HEAD", "text-html").
                 build();
      }

      @GET
      @Path(value = "/sub")
      public Response getSub()
      {
         return Response.ok("CTS-get text/plain").header("CTS-HEAD",
                 "sub-text-plain").
                 build();
      }

      @GET
      @Path(value = "/sub")
      @Produces(value = "text/html")
      public Response headSub()
      {
         return Response.ok(html_content).header("CTS-HEAD", "sub-text-html").
                 build();
      }
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      deployment.getRegistry().addPerRequestResource(Resource.class);
      client = ResteasyClientBuilder.newClient();
   }
   
   @AfterClass
   public static void tearDown()
   {
      client.close();
   }

   /*
    * Client invokes Head on root resource at /GetTest;
    *                 which no request method designated for HEAD;
    *                 Verify that corresponding GET Method is invoked.
    */
   @Test
   public void testHead() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/GetTest"));
      Builder builder = target.request().accept("text/plain");
      Response response = builder.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaderString("CTS-HEAD");
      Assert.assertEquals("text-plain", header);
      response.close();
   }

   /*
    * Client invokes HEAD on root resource at /GetTest;
    *                 which no request method designated for HEAD;
    *                 Verify that corresponding GET Method is invoked.
    */
   @Test
   public void testHead2() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/GetTest"));
      Builder builder = target.request().accept("text/html");
      Response response = builder.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaderString("CTS-HEAD");
      Assert.assertEquals("text-html", header);
      response.close();
   }

   /*
    * Client invokes HEAD on sub resource at /GetTest/sub;
    * which no request method designated for HEAD;
    * Verify that corresponding GET Method is invoked instead.
    */
   @Test
   public void testHeadSubresource() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/GetTest/sub"));
      Builder builder = target.request().accept("text/plain");
      Response response = builder.head();
      Assert.assertEquals(200, response.getStatus());
      String header = response.getHeaderString("CTS-HEAD");
      Assert.assertEquals("sub-text-plain", header);
      response.close();
   }

   /*
    * If client invokes OPTIONS and there is no request method that exists, verify that an automatic response is
    * generated
    */
   @Test
   public void testOptions() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/GetTest/sub"));
      Response response = target.request().options();
      Assert.assertEquals(200, response.getStatus());
      String allowedHeader = response.getHeaderString("Allow");
      Assert.assertNotNull(allowedHeader);
      String[] allowed = allowedHeader.split(",");
      HashSet<String> set = new HashSet<String>();
      for (String allow : allowed)
      {
         set.add(allow.trim());
      }

      Assert.assertTrue(set.contains("GET"));
      Assert.assertTrue(set.contains("OPTIONS"));
      Assert.assertTrue(set.contains("HEAD"));
      response.close();
   }

}
