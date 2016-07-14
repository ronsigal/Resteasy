package org.jboss.resteasy.test.resteasy1161;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.resteasy1161.StdQueryBeanParam;
import org.jboss.resteasy.resteasy1161.TestApplication;
import org.jboss.resteasy.resteasy1161.TestResource;
import org.jboss.resteasy.resteasy1161.TestSubResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for RESTEASY-1161
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 20, 2015
 */
@RunWith(Arquillian.class)
public class TestSubresourceValidation
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      System.out.println("entering createTestArchive()");
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1161.war")
            .addClasses(TestApplication.class, TestResource.class, TestSubResource.class, StdQueryBeanParam.class)
            .addAsWebInfResource("1161/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testSubresource() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8080/RESTEASY-1161/sub/17?limit=abcdef").request().get();
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      for(Iterator<ResteasyConstraintViolation> it = r.getParameterViolations().iterator(); it.hasNext(); )
      {
         System.out.println(it.next().toString());
      }
      countViolations(r, 0, 0, 0, 2, 0);
      assertEquals(400, response.getStatus());
   }
   
   @Test
   public void testReturnValue() throws Exception
   {
      Response response = ClientBuilder.newClient().target("http://localhost:8080/RESTEASY-1161/sub/return/abcd").request().get();
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      for(Iterator<ResteasyConstraintViolation> it = r.getParameterViolations().iterator(); it.hasNext(); )
      {
         System.out.println(it.next().toString());
      }
      countViolations(r, 0, 0, 0, 0, 1);
      assertEquals(500, response.getStatus());
   }
   
   protected void countViolations(ViolationReport e, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
