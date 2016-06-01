package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Locale;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 30, 2013
 */
public class TestValidationHibernateI18N
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static Locale defaultLocale;
   
   @Path("")
   public static class TestResource 
   {
      @GET
      @Path("test")
      @Size(min=2)                                                                                              
      public String test()
      {
         return "a";
      }
   }
   
   //////////////////////////////////////////////////////////////////////////////
   public static void before(Class<?> resourceClass) throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(resourceClass);
      defaultLocale = Locale.getDefault();
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
      Locale.setDefault(defaultLocale);
   }

   @Test
   public void testI18NSetAcceptLanguage() throws Exception
   {
      before(TestResource.class);
      doTestI18NSetAcceptLanguage("fr", "la taille doit être entre");
      doTestI18NSetAcceptLanguage("es", "el tama\u00F1o tiene que estar entre");
      after();
   }
   
   protected void doTestI18NSetAcceptLanguage(String locale, String expectedMessage) throws Exception
   {
      Builder request = ClientBuilder.newClient().target(generateURL("/test")).request();
      request.accept(MediaType.APPLICATION_XML);
      request.header(HttpHeaderNames.ACCEPT_LANGUAGE, locale);
      Response response = request.get();
      ViolationReport report = response.readEntity(ViolationReport.class);
      System.out.println("report: " + report.toString());
      String message = report.getReturnValueViolations().iterator().next().getMessage();
      System.out.println("message: " + message);
      countViolations(report, 1, 0, 0, 0, 0, 1);
      Assert.assertTrue(message.startsWith(expectedMessage)); 
   }
   
   protected void countViolations(ViolationReport e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}