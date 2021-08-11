package org.jboss.resteasy.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartTest {

   private static PartType partType = new PartType() {

      @Override
      public Class<? extends Annotation> annotationType() {
          return PartType.class;
      }

      @Override
      public String value() {
         return "application/json";
      }
   };
   
   private static UndertowJaxrsServer server;
   private static Client client;
   private static TestResource resource;
   private static Annotation[] annotations = new Annotation[] {partType};
   private static MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
   
   @Path("/")
   public interface TestResource {

      @POST
      @Path("multi")
      @Consumes("multipart/mixed")
      @Produces("text/plain")
      @PartType("application/json")
      public String list( @PartType("application/json") List<String> list);
      
      @POST
      @Path("file")
      @Consumes("multipart/mixed")
      @Produces("text/plain")
      public String files(List<File> list) throws Exception;
   }
   
   @Path("/")
   public static class TestResourceImpl {

      @POST
      @Path("multi")
      @Consumes("multipart/mixed")
      @Produces("text/plain")
      public String list(List<String> list) {
         for (String s : list) {
            System.out.println(s);
         }
         return "xyz";
      }
      
      @POST
      @Path("file")
      @Consumes("multipart/mixed")
      @Produces("text/plain")
      public String files(List<File> list) throws Exception {
         for (File f : list) {
            FileInputStream fis = new FileInputStream(f);
            int i = fis.read();
            StringBuffer sb = new StringBuffer();
            while (i != -1) {
               sb.append((char) i);
            }
            System.out.println(sb.toString());
         }
         return "xyz";
      }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResourceImpl.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
      client = ClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081/multi");
      resource = target.proxy(TestResource.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
      server.stop();
   }

//   @Test
   public void testList() {
      Builder request = client.target("http://localhost:8081/multi").request();
      List<String> list = new ArrayList<String>();
      list.add("abc");
      list.add("def");
      Response response = request.post(Entity.entity(list, MULTIPART_MIXED, annotations));
      System.out.println(response.readEntity(String.class));
   }
   
//   @Test
   public void testFile() throws IOException {
      String dir = new java.io.File( "./src/test/java" ).getCanonicalPath();
      String path = this.getClass().getPackage().getName().replace(".", "/");
      List<File> list = new ArrayList<File>();
      File abc = new File(dir + "/" + path + "/abc");
      File def = new File(dir + "/" + path + "/def");
      list.add(abc);
      list.add(def);
      Builder request = client.target("http://localhost:8081/multi").request();
      Response response = request.post(Entity.entity(list, MULTIPART_MIXED, annotations));
      System.out.println(response.readEntity(String.class));
   }
   
   @Test
   public void testProxy() throws Exception {
      String dir = new java.io.File( "./src/test/java" ).getCanonicalPath();
      String path = this.getClass().getPackage().getName().replace(".", "/");
      List<File> list = new ArrayList<File>();
      File abc = new File(dir + "/" + path + "/abc");
      File def = new File(dir + "/" + path + "/def");
      list.add(abc);
      list.add(def);
      resource.files(list);
//      Builder request = client.target("http://localhost:8081/multi").request();
//      Response response = request.post(Entity.entity(list, MULTIPART_MIXED, annotations));
//      System.out.println(response.readEntity(String.class));
   }
   
   /*
      ClassLoader classLoader = getClass().getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream(abc.getAbsolutePath());
      System.out.println(abc.getAbsolutePath());
      new FileInputStream(abc);
    */
}

