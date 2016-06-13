package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.Types;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class ParameterizedSubResourceTest
{
   private static Dispatcher dispatcher;
   private static Client client;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(RootImpl.class);
      dispatcher.getRegistry().addPerRequestResource(GenericSub.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   public static interface Root
   {
      @Path("sub/{path}")
      public Sub getSub(@PathParam("path") String path);
   }

   public static interface Sub
   {
      @GET
      @Produces("text/plain")
      public String get();
   }

   public static interface InternalInterface<T extends Number>
   {
      @PUT
      void foo(T value);
   }

   @Path("/path")
   public static class RootImpl implements Root
   {
      @Override
      public SubImpl<Integer> getSub(String path)
      {
         return new SubImpl<Integer>(path)
         {
         };
      }

   }

   public static class ConcreteSubImpl extends SubImpl<Integer>
   {
      public ConcreteSubImpl(String path)
      {
         super(path);
      }
   }

   public static class SubImpl<T extends Number> implements Sub, InternalInterface<T>
   {
      private final String path;

      public SubImpl(String path)
      {
         this.path = path;
      }

      @Override
      public String get()
      {
         return "Boo! - " + path;
      }

      @Override
      public void foo(T value)
      {
         System.out.println("foo: " + value);
      }

   }


   public interface GenericInterface<T>
   {
      @GET
      public String get(@QueryParam("foo") List<T> params);
   }

   public interface DoubleInterface extends GenericInterface<Double>
   {
   }

   @Path("generic")
   public static class GenericSub
   {
      @Path("sub")
      public DoubleInterface doit()
      {
         InvocationHandler handler = new InvocationHandler()
         {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
               List<Double> list = (List<Double>)args[0];
               return list.get(0).toString();
            }
         };
         Class[] intfs = {DoubleInterface.class};
         return (DoubleInterface)Proxy.newProxyInstance(GenericSub.class.getClassLoader(), intfs, handler);
      }
   }

   @Test
   public void testParametized() throws Exception
   {
      Type[] types = Types.findParameterizedTypes(ConcreteSubImpl.class, InternalInterface.class);
      System.out.println("done");
   }

   @Test
   public void test()
   {
      try
      {
         Builder builder = client.target(generateURL("/path/sub/fred")).request();
         Response response = builder.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("Boo! - fred", response.readEntity(String.class));
      }

      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   @Test
   public void test2()
   {
      try
      {
         WebTarget target = client.target(generateURL("/generic/sub"));
         Response response = target.queryParam("foo", "42.0").request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("42.0", response.readEntity(String.class));
      }

      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

}