package org.jboss.resteasy.test.interceptors;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * RESTEASY-433
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreProcessorExceptionMapperTest extends BaseResourceTest
{
   public static class CandlepinException extends RuntimeException
   {
      private static final long serialVersionUID = 1L;
   }

   public static class CandlepinUnauthorizedException extends CandlepinException
   {
      private static final long serialVersionUID = 1L;
   }


   @Provider
   public static class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>
   {
      public Response toResponse(RuntimeException exception)
      {
         return Response.status(412).build();
      }
   }

   @Provider
   public static class PreProcessSecurityFilter implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         throw new CandlepinUnauthorizedException();
      }
   }

   @Path("/interception")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }
   }

   @Before
   public void setUp() throws Exception
   {
      deployment.getProviderFactory().registerProvider(PreProcessSecurityFilter.class);
      deployment.getProviderFactory().registerProvider(RuntimeExceptionMapper.class);
      addPerRequestResource(MyResource.class);
   }

   @Test
   public void testMapper() throws Exception
   {
      Response res = ClientBuilder.newClient().target(TestPortProvider.generateURL("/interception")).request().get();
      Assert.assertEquals(412, res.getStatus());
      res.close();

   }

}
