package org.jboss.resteasy.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.protobuf.GRPCProvider;
import org.jboss.resteasy.plugins.protobuf.ProtobufProvider;
import org.jboss.resteasy.plugins.protobuf.UndertowJaxrsServerHttp2;
import org.jboss.resteasy.plugins.providers.ByteArrayProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

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
   private static Client client;

   @Path("/")
   public static class TestResource {

      @Context HttpHeaders headers;
      
      @POST
      @Path("helloworld.Greeter/SayHello")
      @Consumes("application/grpc")
      @Produces("application/grpc")
      public HelloReply http2(HelloRequest request) {
         MultivaluedMap<String, String> map = headers.getRequestHeaders();
         for (Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "->");
            for (String s : entry.getValue()) {
               System.out.println("  " + s);
            }
         }
         return HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
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
      client = ClientBuilder.newClient();
      client.register(ProtobufProvider.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
      server.stop();
   }

   @Provider
   @Consumes("application/grpc")
   @Priority(-1111)
   public static class TestProvider extends ByteArrayProvider {

      @Override
      public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      @Override
      public byte[] readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         byte b = (byte) entityStream.read();
         while ( b != -1) {
            printByte(b);
            b = (byte) entityStream.read();
         }
         return "ok".getBytes();
      }

   }

   private static void printByte(byte b) {
//      if (b >= 0) {
//         System.out.println(b);
//         return;
//      }
      for (int i = 7; i >= 0; i--) {
         System.out.print(b >> i & 1);
      }
      System.out.println("");
   }
}
