package org.jboss.resteasy.test.interceptors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GzipTest extends BaseResourceTest
{
   private static Client client;
   
   @Path("/")
   public static interface IGZIP
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public String getText();

      @GET
      @Path("encoded/text")
      @GZIP
      public String getGzipText();

      @GET
      @Path("encoded/text/error")
      @GZIP
      public String getGzipErrorText();

   }

   @Path("/")
   public static class GZIPService
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public Response getText(@Context HttpHeaders headers)
      {
         String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
         System.out.println(acceptEncoding);
         Assert.assertEquals("gzip, deflate", acceptEncoding);
         return Response.ok("HELLO WORLD").header("Content-Encoding", "gzip").build();
      }

      @GET
      @Path("encoded/text")
      @GZIP
      public String getGzipText()
      {
         return "HELLO WORLD";
      }

      @GET
      @Path("bytes")
      @GZIP
      @Produces("text/plain")
      public byte[] getBytes()
      {
         return "HELLO WORLD".getBytes();
      }

      @GET
      @Path("error")
      @GZIP
      @Produces({"application/json;charset=UTF-8"})
      public StreamingOutput getTest()
      {
         return new StreamingOutput()
         {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException
            {
               throw new WebApplicationException(405);
            }
         };
      }

      @GET
      @Path("encoded/text/error")
      @GZIP
      public String getGzipErrorText()
      {
         throw new WebApplicationException(
                 Response.status(500).entity("Hello").type("text/plain").build()
         );
      }

      @PUT
      @Consumes("text/plain")
      @Path("stream")
      public void putStream(InputStream is) throws Exception
      {
         byte[] bytes = ReadFromStream.readFromStream(1024, is);
         String str = new String(bytes);
         Assert.assertEquals("hello world", str);
      }

      @PUT
      @Consumes("text/plain")
      @Path("text")
      public void putText(String text) throws Exception
      {
         Assert.assertEquals("hello world", text);
      }
   }


   @BeforeClass
   public static void beforeSub()
   {
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterSub()
   {
      client.close();
   }
   
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(GZIPService.class);
   }

   @Test
   public void testRawStreams() throws Exception
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPEncodingInterceptor.EndableGZIPOutputStream os = new GZIPEncodingInterceptor.EndableGZIPOutputStream(baos);
      os.write("hello world".getBytes());
      os.finish();
      os.close();

      byte[] bytes1 = baos.toByteArray();
      System.out.println(bytes1.length);
      System.out.println(new String(bytes1));
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes1);
      GZIPDecodingInterceptor.FinishableGZIPInputStream is = new GZIPDecodingInterceptor.FinishableGZIPInputStream(bis);
      byte[] bytes = ReadFromStream.readFromStream(1024, is);
      is.finish();
      String str = new String(bytes);
      Assert.assertEquals("hello world", str);


   }


   @Test
   public void testProxy() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
      IGZIP proxy = target.proxy(IGZIP.class);
      Assert.assertEquals("HELLO WORLD", proxy.getText());
      Assert.assertEquals("HELLO WORLD", proxy.getGzipText());

      // resteasy-651
      try
      {
         proxy.getGzipErrorText();
         Assert.fail("unreachable");
      }
      catch (InternalServerErrorException failure)
      {
         Assert.assertEquals(500, failure.getResponse().getStatus());
         String txt = (String) failure.getResponse().readEntity(String.class);
         Assert.assertEquals("Hello", txt);
      }
   }

   /**
    * RESTEASY-692
    *
    * @throws Exception
    */
   @Test
   public void testContentLength() throws Exception
   {
      {
         Response response = client.target(TestPortProvider.generateURL("/text")).request().get();
         Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));
         String cl = response.getHeaderString("Content-Length");
         if (cl != null)
         {
            // make sure the content length is greater than 11 because this will be a gzipped encoding
            Assert.assertTrue(Integer.parseInt(cl) > 11);
         }
      }
      {
         Response response = client.target(TestPortProvider.generateURL("/bytes")).request().get();
         String cl = response.getHeaderString("Content-Length");
         if (cl != null)
         {
            // make sure the content length is greater than 11 because this will be a gzipped encoding
            int i = Integer.parseInt(cl);
            System.out.println("***");
            System.out.println("Content-Length: " + i);
            System.out.println("***");
            Assert.assertTrue(i > 11);
         }
         Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));
      }
   }

   @Test
   public void testRequestError() throws Exception
   {
      Response response = client.target(TestPortProvider.generateURL("/error")).request().get();
      Assert.assertEquals(405, response.getStatus());
      response.close();

   }

   @Test
   public void testPutStream() throws Exception
   {
      Builder builder = client.target(TestPortProvider.generateURL("/stream")).request();
      builder.header("Content-Encoding", "gzip");
      Response res = builder.put(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }

   @Test
   public void testPutText() throws Exception
   {
      Builder builder = client.target(TestPortProvider.generateURL("/text")).request();
      builder.header("Content-Encoding", "gzip");
      Response res = builder.put(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(204, res.getStatus());
      res.close();
   }

   @Test
   public void testRequest() throws Exception
   {
      Builder builder = client.target(TestPortProvider.generateURL("/text")).request();
      Response response = builder.get();
      Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));

   }

   @Test
   public void testRequest2() throws Exception
   {
      Builder builder = client.target(TestPortProvider.generateURL("/encoded/text")).request();
      Response response = builder.get();
      Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));

   }

   @Test
   public void testWasZipped() throws Exception
   {
      // test that it was zipped by running it through Apache HTTP Client which does not automatically unzip

      HttpClient client = new DefaultHttpClient();
      {
         HttpGet get = new HttpGet(TestPortProvider.generateURL("/encoded/text"));
         get.addHeader("Accept-Encoding", "gzip, deflate");
         HttpResponse response = client.execute(get);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
         Assert.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         String entity = EntityUtils.toString(response.getEntity());
         System.out.println(entity);
         Assert.assertNotSame(entity, "HELLO WORLD");
      }


      {
         HttpGet get = new HttpGet(TestPortProvider.generateURL("/text"));
         get.addHeader("Accept-Encoding", "gzip, deflate");
         HttpResponse response = client.execute(get);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
         Assert.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         String entity = EntityUtils.toString(response.getEntity());
         Assert.assertNotSame(entity, "HELLO WORLD");
      }
   }

   @Test
   public void testWithoutAcceptEncoding() throws Exception
   {
      // test that if there is no accept-encoding: gzip header that result isn't encoded

      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(TestPortProvider.generateURL("/encoded/text"));
      HttpResponse response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      Assert.assertNull(response.getFirstHeader("Content-Encoding"));

      // test that it is actually zipped
      String entity = EntityUtils.toString(response.getEntity());
      Assert.assertEquals(entity, "HELLO WORLD");

   }


}
