package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * RESTEASY-300
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperInjectionTest extends BaseResourceTest
{
   private static Client client;
   
   public static class MyException extends RuntimeException
   {
   }

   public static class MyException2 extends RuntimeException
   {
   }

   public static class MyExceptionMapper implements ExceptionMapper<MyException>
   {
      @Context
      Request request;

      public Response toResponse(MyException exception)
      {
         System.out.println("Method: " + request.getMethod());

         ArrayList<Variant> list = new ArrayList<Variant>();
         list.add(new Variant(MediaType.APPLICATION_JSON_TYPE, (String)null, null));
         request.selectVariant(list);
         return Response.status(Response.Status.PRECONDITION_FAILED).build();
      }
   }

   public static class MyException2Mapper implements ExceptionMapper<MyException2>
   {
      public Response toResponse(MyException2 exception)
      {
         return null;
      }
   }

   public static class NotFoundExceptionMapper implements
           ExceptionMapper<NotFoundException>
   {
      @Context
      HttpHeaders httpHeaders;

      public Response toResponse(NotFoundException exception)
      {
         System.out.println(httpHeaders.getRequestHeaders());
         System.out.println("Mapped!");
         return Response.status(505).build();
      }
   }


   @Path("/test")
   public static class MyService
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         throw new MyException();
      }

      @Path("/null")
      @GET
      @Produces("text/plain")
      public String getNull()
      {
         throw new MyException2();
      }
   }

   @BeforeClass
   public static void beforeClass()
   {
      client = ClientBuilder.newClient();
   }
   
   @AfterClass
   public static void afterClass()
   {
      client.close();
   }
   
   @Before
   public void init() throws Exception
   {
      getProviderFactory().registerProviderInstance(new MyExceptionMapper());
      getProviderFactory().registerProviderInstance(new MyException2Mapper());
      getProviderFactory().registerProvider(NotFoundExceptionMapper.class);
      addPerRequestResource(MyService.class);
   }

   /**
    * RESTEASY-396
    *
    * @throws Exception
    */
   @Test
   public void testNotFound() throws Exception
   {
      Response response = client.target(generateBaseUrl() + "/test/nonexistent").request().get();
      Assert.assertEquals(505, response.getStatus());
      response.close();
   }

   @Test
   public void testMapper() throws Exception
   {
      Response response = client.target(generateBaseUrl() + "/test").request().get();
      Assert.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
      response.close();
   }

   @Test
   public void testMapper2() throws Exception
   {
      Response response = client.target(generateBaseUrl() + "/test/null").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();
   }

}
