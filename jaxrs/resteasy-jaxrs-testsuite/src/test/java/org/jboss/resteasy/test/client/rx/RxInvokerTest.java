package org.jboss.resteasy.test.client.rx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerImpl;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date March 9, 2016
 */
public class RxInvokerTest
{
   protected final GenericType<String> STRING_TYPE = new GenericType<String>() {};
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static Client client;
   protected static TestExecutor executor = new TestExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

   @HttpMethod("TRACE")
   @Target(value= ElementType.METHOD)
   @Retention(value= RetentionPolicy.RUNTIME)
   public @interface TRACE {
   }
   
   @HttpMethod("METHOD")
   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface METHOD {
   }
   
   public static class TestExecutor extends ThreadPoolExecutor
   {
      public boolean used;
      
      public TestExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
      {
         super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
      }
      
      public void execute(Runnable command)
      {
         used = true;
         super.execute(command);
      }
   }
   
   public static class TestRxInvoker extends CompletionStageRxInvokerImpl
   {
      public static boolean used;
      
      public TestRxInvoker()
      {
         used = true;
      }
      
      public TestRxInvoker(ExecutorService executor)
      {
         super(executor);
         used = true;
      }
      
      public TestRxInvoker(ClientInvocationBuilder builder)
      {
         super(builder);
         used = true;
      }
      
      public TestRxInvoker(ClientInvocationBuilder builder, ExecutorService executor)
      {
         super(builder, executor);
         used = true;
      }
   }
   
   @Path("")
   public static class TestResource
   {
      @Path("get")
      @GET
      public Response get()
      {
         return Response.ok("get").build();
      }
      
      @Path("put")
      @PUT
      @Consumes(MediaType.TEXT_PLAIN)
      public Response put(String s)
      {
         return Response.ok(s).build();
      }
      
      @Path("post")
      @POST
      @Consumes(MediaType.TEXT_PLAIN)
      public Response post(String s)
      {
         return Response.ok(s).build();
      }
      
      @Path("delete")
      @DELETE
      public Response delete()
      {
         return Response.ok("delete").build();
      }
      
      @Path("head")
      @HEAD
      public Response head()
      {
         return Response.noContent().header("key", "head").build();
      }
      
      @Path("options")
      @OPTIONS
      public Response options()
      {
         return Response.ok("options").build();
      }
      
      @Path("trace")
      @TRACE
      public Response trace()
      {
         return Response.ok("trace").build();
      }
      
      @Path("method")
      @METHOD
      public Response method()
      {
         return Response.ok("method").build();
      }
      
      @Path("methodEntity")
      @METHOD
      public Response methodEntity(String s)
      {
         return Response.ok(s).build();
      }
   }

   static RxInvoker<?> buildInvoker(Builder builder, boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      if (useCustomInvoker)
      {
         if (useExecutor)
         {
            return builder.rx(TestRxInvoker.class, executor).builder(builder);
         }
         else
         {
            return builder.rx(TestRxInvoker.class).builder(builder);
         }
      }
      else
      {
         if (useExecutor)
         {
            return builder.rx(executor);
         }
         else
         {
            return builder.rx();
         }
      }
   }
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   public static void reset()
   {
      executor.used = false;
      TestRxInvoker.used = false;
   }

   @Test
   public void TestRxClientGet() throws Exception
   {
      doTestRxClientGet(false, false);
      doTestRxClientGet(false, true);
      doTestRxClientGet(true, false);
      doTestRxClientGet(true, true);
   }
   
   void doTestRxClientGet(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/get").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.get();
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("get", response);
      Assert.assertEquals(useCustomInvoker, TestRxInvoker.used);
      Assert.assertEquals(useExecutor, executor.used);
   }

   @Test
   public void testRxClientGetClass() throws Exception
   {
      doTestRxClientGetClass(false, false);
      doTestRxClientGetClass(false, true);
      doTestRxClientGetClass(true, false);
      doTestRxClientGetClass(true, true);
   }
   
   void doTestRxClientGetClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/get").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(String.class);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientGetGenericType() throws Exception
   {
      doTestRxClientGetGenericType(false, false);
      doTestRxClientGetGenericType(false, true);
      doTestRxClientGetGenericType(true, false);
      doTestRxClientGetGenericType(true, true);
   }
   
   void doTestRxClientGetGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/get").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(STRING_TYPE);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPut() throws Exception
   {
      doTestRxClientPut(false, false);
      doTestRxClientPut(false, true);
      doTestRxClientPut(true, false);
      doTestRxClientPut(true, true);
   }
   
   void doTestRxClientPut(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/put").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPutClass() throws Exception
   {
      doTestRxClientPutClass(false, false);
      doTestRxClientPutClass(false, true);
      doTestRxClientPutClass(true, false);
      doTestRxClientPutClass(true, true);
   }
   
   void doTestRxClientPutClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {   
      reset();
      Builder builder = client.target("http://localhost:8081/put").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPutGenericType() throws Exception
   {
      doTestRxClientPutGenericType(false, false);
      doTestRxClientPutGenericType(false, true);
      doTestRxClientPutGenericType(true, false);
      doTestRxClientPutGenericType(true, true);
   }
   
   void doTestRxClientPutGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/put").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPost() throws Exception
   {
      doTestRxClientPost(false, false);
      doTestRxClientPost(false, true);
      doTestRxClientPost(true, false);
      doTestRxClientPost(true, true);
   }
   
   void doTestRxClientPost(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/post").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPostClass() throws Exception
   {
      doTestRxClientPostClass(false, false);
      doTestRxClientPostClass(false, true);
      doTestRxClientPostClass(true, false);
      doTestRxClientPostClass(true, true);
   }

   void doTestRxClientPostClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/post").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientPostGenericType() throws Exception
   {
      dotestRxClientPostGenericType(false, false);
      dotestRxClientPostGenericType(false, true);
      dotestRxClientPostGenericType(true, false);
      dotestRxClientPostGenericType(true, true);
   }
   
   void dotestRxClientPostGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/post").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientDelete() throws Exception
   {
      doTestRxClientDelete(false, false);
      doTestRxClientDelete(false, true);
      doTestRxClientDelete(true, false);
      doTestRxClientDelete(true, true);
   }
   
   void doTestRxClientDelete(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/delete").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.delete();
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("delete", response);
      Assert.assertEquals(useExecutor, executor.used);
   }

   @Test
   public void testRxClientDeleteClass() throws Exception
   {
      doTestRxClientDeleteClass(false, false);
      doTestRxClientDeleteClass(false, true);
      doTestRxClientDeleteClass(true, false);
      doTestRxClientDeleteClass(true, true);
   }
   
   void doTestRxClientDeleteClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/delete").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(String.class);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientDeleteGenericType() throws Exception
   {
      doTestRxClientDeleteGenericType(false, false);
      doTestRxClientDeleteGenericType(false, true);
      doTestRxClientDeleteGenericType(true, false);
      doTestRxClientDeleteGenericType(true, true);
   }
   
   void doTestRxClientDeleteGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/delete").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(STRING_TYPE);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientHead() throws Exception
   {
      doTestRxClientHead(false, false);
      doTestRxClientHead(false, true);
      doTestRxClientHead(true, false);
      doTestRxClientHead(true, true);
   }
   
   void doTestRxClientHead(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/head").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.head();
      Response response = cs.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(204, response.getStatus());
      Assert.assertEquals("head", response.getStringHeaders().getFirst("key"));
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientOptions() throws Exception
   {
      doTestRxClientOptions(false, false);
      doTestRxClientOptions(false, true);
      doTestRxClientOptions(true, false);
      doTestRxClientOptions(true, true);
   }
   
   void doTestRxClientOptions(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/options").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.options();
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("options", response);
      Assert.assertEquals(useExecutor, executor.used);
   }

   @Test
   public void testRxClientOptionsClass() throws Exception
   {
      doTestRxClientOptionsClass(false, false);
      doTestRxClientOptionsClass(false, true);
      doTestRxClientOptionsClass(true, false);
      doTestRxClientOptionsClass(true, true);
   }
   
   void doTestRxClientOptionsClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/options").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(String.class);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientOptionsGenericType() throws Exception
   {
      doTestRxClientOptionsGenericType(false, false);
      doTestRxClientOptionsGenericType(false, true);
      doTestRxClientOptionsGenericType(true, false);
      doTestRxClientOptionsGenericType(true, true);
   }
   
   void doTestRxClientOptionsGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/options").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(STRING_TYPE);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientTrace() throws Exception
   {
      doTestRxClientTrace(false, false);
      doTestRxClientTrace(false, true);
      doTestRxClientTrace(true, false);
      doTestRxClientTrace(true, true);
   }
   
   void doTestRxClientTrace(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/trace").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.trace();
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("trace", response);
      Assert.assertEquals(useExecutor, executor.used);
   }

   @Test
   public void testRxClientTraceClass() throws Exception
   {
      doTestRxClientTraceClass(false, false);
      doTestRxClientTraceClass(false, true);
      doTestRxClientTraceClass(true, false);
      doTestRxClientTraceClass(true, true);
   }
   
   void doTestRxClientTraceClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/trace").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(String.class);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientTraceGenericType() throws Exception
   {
      reset();
      doTestRxClientTraceGenericType(false, false);
      doTestRxClientTraceGenericType(false, true);
      doTestRxClientTraceGenericType(true, false);
      doTestRxClientTraceGenericType(true, true);
   }
   
   void doTestRxClientTraceGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/trace").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(STRING_TYPE);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientMethod() throws Exception
   {
      doTestRxClientMethod(false, false);
      doTestRxClientMethod(false, true);
      doTestRxClientMethod(true, false);
      doTestRxClientMethod(true, true);
   }
   
   void doTestRxClientMethod(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/method").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD");
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("method", response);
      Assert.assertEquals(useExecutor, executor.used);
   }

   @Test
   public void testRxClientMethodClass() throws Exception
   {
      doTestRxClientMethodClass(false, false);
      doTestRxClientMethodClass(false, true);
      doTestRxClientMethodClass(true, false);
      doTestRxClientMethodClass(true, true);
   }
   
   void doTestRxClientMethodClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/method").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", String.class);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientMethodGenericType() throws Exception
   {
      doTestRxClientMethodGenericType(false, false);
      doTestRxClientMethodGenericType(false, true);
      doTestRxClientMethodGenericType(true, false);
      doTestRxClientMethodGenericType(true, true);
   }
   
   void doTestRxClientMethodGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/method").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", STRING_TYPE);
      System.out.println("response: " + cs.get());
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientMethodEntity() throws Exception
   {
      doTestRxClientMethodEntity(false, false);
      doTestRxClientMethodEntity(false, true);
      doTestRxClientMethodEntity(true, false);
      doTestRxClientMethodEntity(true, true);
   }
   
   void doTestRxClientMethodEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/methodEntity").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      System.out.println("response: " + response);
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientMethodClassEntity() throws Exception
   {
      doTestRxClientMethodClassEntity(false, false);
      doTestRxClientMethodClassEntity(false, true);
      doTestRxClientMethodClassEntity(true, false);
      doTestRxClientMethodClassEntity(true, true);
   }
   
   void doTestRxClientMethodClassEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/methodEntity").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
   
   @Test
   public void testRxClientMethodGenericTypeEntity() throws Exception
   {
      doTestRxClientMethodGenericTypeEntity(false, false);
      doTestRxClientMethodGenericTypeEntity(false, true);
      doTestRxClientMethodGenericTypeEntity(true, false);
      doTestRxClientMethodGenericTypeEntity(true, true);
   }
   
   void doTestRxClientMethodGenericTypeEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target("http://localhost:8081/methodEntity").request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      System.out.println("response: " + response);
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, executor.used);
   }
}
