package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * RESTEASY-421
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapperRuntimeException2Test extends BaseResourceTest
{
   public static class MyException extends RuntimeException
   {
   }

   public static class MyExceptionMapper implements ExceptionMapper<ApplicationException>
   {
      public Response toResponse(ApplicationException exception)
      {
         return Response.status(Response.Status.PRECONDITION_FAILED).build();
      }
   }

   public static class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>
   {
      public Response toResponse(RuntimeException exception)
      {
         return Response.serverError().build();
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
   }

   @Before
   public void init() throws Exception
   {
      getProviderFactory().registerProviderInstance(new MyExceptionMapper());
      getProviderFactory().registerProviderInstance(new RuntimeExceptionMapper());
      addPerRequestResource(MyService.class);
   }


   @Test
   public void testMapper() throws Exception
   {
      Response response = ClientBuilder.newClient().target(generateBaseUrl() + "/test").request().get();
      Assert.assertEquals(Response.Status.PRECONDITION_FAILED.getStatusCode(), response.getStatus());
      response.close();
   }


}