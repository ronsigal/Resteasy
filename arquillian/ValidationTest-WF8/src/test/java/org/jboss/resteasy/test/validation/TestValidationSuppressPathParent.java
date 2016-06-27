package org.jboss.resteasy.test.validation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.validation.Foo;
import org.junit.Assert;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 16, 2012
 */
public class TestValidationSuppressPathParent
{
   
   public void doTestInputViolations(String fieldPath, String propertyPath, String classPath, String parameterPath) throws Exception
   {
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/all/a/z").request();
      Foo foo = new Foo("p");
      Response response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(400, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      String entity = response.readEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(entity);
      countViolations(e, 4, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation violation = e.getFieldViolations().iterator().next();
      System.out.println("field path: " + violation.getPath());
      Assert.assertEquals(fieldPath, violation.getPath());
      violation = e.getPropertyViolations().iterator().next();
      System.out.println("property path: " + violation.getPath());
      Assert.assertEquals(propertyPath, violation.getPath());
      violation = e.getClassViolations().iterator().next();
      System.out.println("class path: " + violation.getPath());
      Assert.assertEquals(classPath, violation.getPath());
      violation = e.getParameterViolations().iterator().next();
      System.out.println("parameter path: " + violation.getPath());
      Assert.assertEquals(parameterPath, violation.getPath());
   }
   
   public void doTestReturnValueViolations(String returnValuePath) throws Exception
   {
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/native").request();
      Foo foo = new Foo("abcdef");
      Response response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(500, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      String entity = response.readEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(entity);
      ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
      System.out.println("return value path: " + violation.getPath());
      Assert.assertEquals(returnValuePath, violation.getPath());
   }

   private void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
