package org.jboss.resteasy.experiment;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestColon {

   private static UndertowJaxrsServer server;
   private static Client client;

   @Path("/")
   public static class TestResource {

      @Context Application application;

      @GET
      @Path("a:b/{p}")
      public void testColon(@PathParam("p") String p) {
         System.out.println("p: " + p);
      }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
      client = (ResteasyClient) ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }

   @Test
   public void test() {
      Builder request = client.target("http://localhost:8081/a:b/c:d").request();
      Response response = request.get();
      System.out.println("status: " + response.getStatus());
      response.close();
   }
}
