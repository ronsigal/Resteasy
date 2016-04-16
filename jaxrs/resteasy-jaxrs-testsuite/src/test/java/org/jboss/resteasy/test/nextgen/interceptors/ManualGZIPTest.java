package org.jboss.resteasy.test.nextgen.interceptors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;;

/**
 * RESTEASY-1170
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class ManualGZIPTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static byte[] zipped;

   @Path("")
   public static class TestResource
   {
      @Path("automatic/false")
      @GET
      @GZIP(automatic=false)
      public Response automaticFalse() throws Exception
      {
         return Response.ok().entity(zipped).build();
      }
      
      @Path("automatic/true")
      @GET
      @GZIP(automatic=true)
      public Response automaticTrue() throws Exception
      {
         return Response.ok().entity(zipped).build();
      }
      
      @Path("automatic/default")
      @GET
      @GZIP
      public Response automaticDefault() throws Exception
      {
         return Response.ok().entity(zipped).build();
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      zipped = zip("test".getBytes());
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testGZIPFalse() throws Exception
   {
      Client client = ClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081/automatic/false");
      Response response = target.request().header("Accept-Encoding", "gzip").get();
      Assert.assertEquals(200, response.getStatus());
      String s = response.readEntity(String.class);
      System.out.println("response: " + s);
      Assert.assertEquals("test", s);
   }
   
   @Test
   public void testGZIPTrue() throws Exception
   {
      Client client = ClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081/automatic/true");
      Response response = target.request().header("Accept-Encoding", "gzip").get();
      Assert.assertEquals(200, response.getStatus());
      InputStream is = response.readEntity(InputStream.class);
      String s = unzip(is);
      System.out.println("response: " + s);
      Assert.assertEquals("test", s);
   }
   
   @Test
   public void testGZIPDefault() throws Exception
   {
      Client client = ClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081/automatic/default");
      Response response = target.request().header("Accept-Encoding", "gzip").get();
      Assert.assertEquals(200, response.getStatus());
      InputStream is = response.readEntity(InputStream.class);
      String s = unzip(is);
      System.out.println("response: " + s);
      Assert.assertEquals("test", s);
   }
   
   private static byte[] zip(byte[] b) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gis = new GZIPOutputStream(baos);
      gis.write(b);
      gis.finish();
      return baos.toByteArray();
   }
   
   private String unzip(InputStream is) throws IOException
   {
      GZIPInputStream gis = new GZIPInputStream(is);
      StringBuffer sb = new StringBuffer();
      int c = gis.read();
      while (c != -1)
      {
         sb.append((char) c);
         c = gis.read();
      }
      return sb.toString();
   }
}
