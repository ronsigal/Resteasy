package org.jboss.resteasy.test.nextgen.validation;

import java.util.Hashtable;

import javax.enterprise.context.RequestScoped;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Size;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ResteasyViolationExceptionMapper;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1186
 * 
 * @author <a href="etay5995@yahoo.com">Eric Taylor</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright August 1, 2015
 */
public class TestInterfaceMethodConstraints
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("root")
   public interface RootResource
   {   
      @Path("/sub")
      SubResource getSubResource();  
   }
   
   public interface SubResource extends ValidResource
   {
      @GET
      @Override
      Response getAll(@BeanParam QueryBeanParamImpl beanParam);  
   }
   
   public interface ValidResource
   {
      Response getAll(@Valid QueryBeanParamImpl beanParam);
   }
   
   @RequestScoped
   public static class RootResourceImpl implements RootResource
   {
      @Override
      public SubResource getSubResource()
      {
         return new SubResourceImpl();
      }
   }
   
   @RequestScoped
   public static class SubResourceImpl implements SubResource
   {
      static boolean methodEntered;
      
      @Override
      public Response getAll(QueryBeanParamImpl beanParam)
      {
         System.out.println("beanParam#getParam valid? " + beanParam.getParam());
         methodEntered = true;
         return Response.ok().build();
      }
   }
   
   public interface QueryBeanParam
   {
      @Size(min = 2)
      String getParam(); 
   }
   
   public static class QueryBeanParamImpl implements QueryBeanParam
   {
      @QueryParam("foo")
      private String param;
      
      @Override
      public String getParam()
      {
         return param;
      }
   }
   
   @Provider
   public static class CustomExceptionMapper extends ResteasyViolationExceptionMapper
   {
      @Override
      public Response toResponse(ValidationException exception)
      {
         System.out.println("CustomExceptionMapper.toResponse()");
         if (SubResourceImpl.methodEntered)
         {
            return Response.status(444).build();
         }
         else
         {
            return super.toResponse(exception);
         }
      }
   }

   @Before
   public void before() throws Exception
   {
      Hashtable<String, String> initParams = new Hashtable<String, String>();
      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(RootResourceImpl.class);
      deployment.getProviderFactory().registerProvider(CustomExceptionMapper.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testRoot() throws Exception
   {  
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(TestPortProvider.generateURL("/root/sub?foo=x")).request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      ViolationReport report = response.readEntity(ViolationReport.class);
      countViolations(report, 0, 0, 0, 1, 0);
   }

   private void countViolations(ViolationReport e, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}