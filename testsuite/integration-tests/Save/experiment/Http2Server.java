package org.jboss.resteasy.experiment;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.protobuf.GRPCProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloWorldProto;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Http2Server {

   public static void main(String[] args) {
      try
      {
         init();
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private static UndertowJaxrsServerHttp2 server;

   @Path("/")
   public static class TestResource {

      @Context HttpHeaders headers;
      
      @POST
      @Path("helloworld.Greeter/SayHello")
      @Produces("application/grpc")
//      public String http2(byte[] bytes) {
      public HelloReply http2(HelloRequest request) {
         MultivaluedMap<String, String> map = headers.getRequestHeaders();
         for (Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "->");
            for (String s : entry.getValue()) {
               System.out.println("  " + s);
            }
         }
         System.out.println("request: " + request);
         HelloReply reply = HelloReply.newBuilder().setMessage("hello " + request.getName()).build();
         return reply;
      }

      //      routeguide.RouteGuide/GetFeature

      @POST
      @Path("routeguide.RouteGuide/GetFeature")
      @Produces("text/plain")
      public String route(byte[] bytes) {
         return "ok";
      }
      
      @POST
      @Path("routeguide.RouteGuide/RecordRoute")
      @Produces ("text/plain")
      public String recordRoute(byte[] bytes) {
         return "recordRoute";
      }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         classes.add(GRPCProvider.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServerHttp2().start();
      server.deploy(MyApp.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }
}
