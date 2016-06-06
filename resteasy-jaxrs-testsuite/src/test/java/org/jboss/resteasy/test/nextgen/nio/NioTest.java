package org.jboss.resteasy.test.nextgen.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Santiago Pericas-Geertsen
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date June 4, 2016
 */
public class NioTest
{  
   private static final int FOUR_KB = 4 * 1024;
   private static UndertowJaxrsServer server;
   private static Client client;
   private static Map<String, byte[]> files = new ConcurrentHashMap<String, byte[]>();

   @ApplicationPath("context")
   public static class TestApplication extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(FileResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
      server.deploy(TestApplication.class);
      client = ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
   }
   
   @Test
   public void upload()
   {
      final byte[] bytes = new byte[FOUR_KB];
      for (int i = 0; i < FOUR_KB; i++)
      {
         bytes[i] = (byte) i;
      }
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      WebTarget target = client.target("http://localhost:8081/context/test/upload").queryParam("path", "abc");
      Response response = target.request(MediaType.APPLICATION_OCTET_STREAM).post(Entity.entity(in, MediaType.APPLICATION_OCTET_STREAM_TYPE));
      System.out.println("status: " + response.getStatus());
   }

   //@Test
   public void uploadClient()
   {
      final byte[] bytes = new byte[FOUR_KB];
      for (int i = 0; i < FOUR_KB; i++)
      {
         bytes[i] = (byte) i;
      }
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      final byte[] buffer = new byte[FOUR_KB];

      WebTarget target = client.target("http://localhost:8081/context/test/upload").queryParam("path", "abc");
      target.request(MediaType.APPLICATION_OCTET_STREAM).nio().post(
            out -> {              // writer handler
               try {
                  final int n = in.read(buffer);
                  if (n >= 0) {
                     out.write(buffer, 0, n);
                     return true;    // more to write
                  }
                  in.close();
                  return false;       // we're done
               } catch (IOException e) {
                  throw new WebApplicationException(e);
               }
            });
   }

   //@Test
   public void downloadClient()
   {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final byte[] buffer = new byte[FOUR_KB];

      WebTarget target = client.target("http://localhost:8081/context/test/upload");
      target.request().accept(MediaType.APPLICATION_OCTET_STREAM).nio().get(
            in -> {                     // reader handler
               try {
                  if (in.isFinished()) {
                     files.put("abc", out.toByteArray());
                     out.close();
                  } else {
                     final int n = in.read(buffer);
                     out.write(buffer, 0, n);
                  }
               } catch (IOException e) {
                  throw new WebApplicationException(e);
               }
            });

   }
}
