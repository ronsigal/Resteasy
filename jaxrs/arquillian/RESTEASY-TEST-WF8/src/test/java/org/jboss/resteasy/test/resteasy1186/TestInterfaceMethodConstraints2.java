package org.jboss.resteasy.test.resteasy1186;

import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.resteasy1186c.AbstractAsyncRootResource;
import org.jboss.resteasy.resteasy1186c.AsyncRootResource;
import org.jboss.resteasy.resteasy1186c.AsyncRootResourceImpl;
import org.jboss.resteasy.resteasy1186c.AsyncSubResource;
import org.jboss.resteasy.resteasy1186c.AsyncSubResourceImpl;
import org.jboss.resteasy.resteasy1186c.AsyncValidResource;
import org.jboss.resteasy.resteasy1186c.QueryBeanParam;
import org.jboss.resteasy.resteasy1186c.QueryBeanParamImpl;
import org.jboss.resteasy.resteasy1186c.RootResource;
import org.jboss.resteasy.resteasy1186c.RootResourceImpl;
import org.jboss.resteasy.resteasy1186c.SubResource;
import org.jboss.resteasy.resteasy1186c.SubResourceImpl;
import org.jboss.resteasy.resteasy1186c.TestApplication;
import org.jboss.resteasy.resteasy1186c.ValidResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1186
 * 
 * @author <a href="etay5995@yahoo.com">Eric Taylor</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 20, 2015
 */
@RunWith(Arquillian.class)
public class TestInterfaceMethodConstraints2
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1186.war")
            .addClasses(TestApplication.class)
            .addClasses(QueryBeanParam.class, QueryBeanParamImpl.class)
            .addClasses(RootResource.class, RootResourceImpl.class, ValidResource.class)
            .addClasses(SubResource.class, SubResourceImpl.class)
            .addClass(AbstractAsyncRootResource.class)
            .addClasses(AsyncRootResource.class, AsyncRootResourceImpl.class)
            .addClasses(AsyncSubResource.class, AsyncSubResourceImpl.class)
            .addClasses(AsyncValidResource.class)
            .addAsWebInfResource("1186/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   //@Test
   public void testRoot() throws Exception
   {  
      System.out.println("starting testRoot() *************");
      Client client = ClientBuilder.newClient();
      Builder builder = client.target("http://localhost:8080/RESTEASY-1186/test/root/sub?foo=x").request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      ViolationReport report = response.readEntity(ViolationReport.class);
      countViolations(report, 0, 0, 0, 1, 0);
      Iterator<ResteasyConstraintViolation> it = report.getParameterViolations().iterator();
      System.out.println("\r" + it.next());
      builder = client.target("http://localhost:8080/RESTEASY-1186/test/root/entered").request();
      response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
   }
   
   //@Test
   public void testAsynch() throws Exception
   {  
      System.out.println("starting testAsynch() *************");
      Client client = ClientBuilder.newClient();
      Builder builder = client.target("http://localhost:8080/RESTEASY-1186/test/async/sub?foo=x").request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(400, response.getStatus());
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      ViolationReport report = response.readEntity(ViolationReport.class);
      countViolations(report, 0, 0, 0, 1, 0);
      Iterator<ResteasyConstraintViolation> it = report.getParameterViolations().iterator();
      System.out.println("\r" + it.next());
//      builder = client.target("http://localhost:8080/RESTEASY-1186/test/async/entered").request();
//      response = builder.get();
//      System.out.println("status: " + response.getStatus());
//      Assert.assertEquals(200, response.getStatus());
   }

   
   /*
   First call (with invalid param value) to '/async/?foo=a' returns a status 500.
   Second call (with valid param value) to '/async/?foo=ab' returns a status 200.
   Third call (with invalid param value) to '/async/?foo=a' returns a status 200.
   Each additional call with an invalid value will return status 200
   */
   @Test
   public void testAsynch2() throws Exception
   {  
      System.out.println("starting testAsynch2() *************");
      
      {
         Client client = ClientBuilder.newClient();
         Builder builder = client.target("http://localhost:8080/RESTEASY-1186/test/async/sub?foo=x").request();
         builder.accept(MediaType.APPLICATION_XML);
         Response response = builder.get();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertTrue(header instanceof String);
         Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
         ViolationReport report = response.readEntity(ViolationReport.class);
         countViolations(report, 0, 0, 0, 1, 0);
         Iterator<ResteasyConstraintViolation> it = report.getParameterViolations().iterator();
         System.out.println("\r" + it.next());
      }
      
      {
         Client client = ClientBuilder.newClient();
         Builder builder = client.target("http://localhost:8080/RESTEASY-1186/test/async/sub?foo=xy").request();
         builder.accept(MediaType.APPLICATION_XML);
         Response response = builder.get();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
      }
      
      {
         Client client = ClientBuilder.newClient();
         Builder builder = client.target("http://localhost:8080/RESTEASY-1186/test/async/sub?foo=x").request();
         builder.accept(MediaType.APPLICATION_XML);
         Response response = builder.get();
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertTrue(header instanceof String);
         Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
         ViolationReport report = response.readEntity(ViolationReport.class);
         countViolations(report, 0, 0, 0, 1, 0);
         Iterator<ResteasyConstraintViolation> it = report.getParameterViolations().iterator();
         System.out.println("\r" + it.next());
      }
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