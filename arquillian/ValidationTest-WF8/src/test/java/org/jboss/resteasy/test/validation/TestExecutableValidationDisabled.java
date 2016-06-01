package org.jboss.resteasy.test.validation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.validation.Foo;
import org.jboss.resteasy.validation.FooConstraint;
import org.jboss.resteasy.validation.FooReaderWriter;
import org.jboss.resteasy.validation.FooValidator;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestClassConstraint;
import org.jboss.resteasy.validation.TestClassValidator;
import org.jboss.resteasy.validation.TestResourceWithAllViolationTypes;
import org.jboss.resteasy.validation.TestResourceWithReturnValues;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Junk 8, 2013
 */
@RunWith(Arquillian.class)
public class TestExecutableValidationDisabled
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(Foo.class, FooConstraint.class, FooReaderWriter.class, FooValidator.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addClasses(TestResourceWithAllViolationTypes.class, TestResourceWithReturnValues.class)
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            .addAsResource("validation-disabled.xml", "META-INF/validation.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testReturnValues() throws Exception
   {
      // Valid native constraint
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/native").request();
      Foo foo = new Foo("a");
      Response response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.readEntity(Foo.class));
      
      // Valid imposed constraint
      request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/imposed").request();
      foo = new Foo("abcde");
      response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.readEntity(Foo.class));
      
      // Valid native and imposed constraints.
      request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/nativeAndImposed").request();
      foo = new Foo("abc");
      response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.readEntity(Foo.class));
      
      {
         // Invalid native constraint
      	// BUT EXECUTABLE VALIDATION IS DISABLED.
         request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/native").request();
         foo = new Foo("abcdef");
         response = request.post(Entity.entity(foo, "application/foo"));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(foo, response.readEntity(Foo.class));
      }
      
      {
         // Invalid imposed constraint
      	// BUT EXECUTABLE VALIDATION IS DISABLED.
         request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/imposed").request();
         foo = new Foo("abcdef");
         response = request.post(Entity.entity(foo, "application/foo"));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(foo, response.readEntity(Foo.class));
      }
      
      {
         // Invalid native and imposed constraints
      	// BUT EXECUTABLE VALIDATION IS DISABLED.
         request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/return/nativeAndImposed").request();
         foo = new Foo("abcdef");
         response = request.post(Entity.entity(foo, "application/foo"));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(foo, response.readEntity(Foo.class));
      }
   }

   @Test
   public void testViolationsBeforeReturnValue() throws Exception
   {
      // Valid
      Builder request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/all/abc/wxyz").request();
      Foo foo = new Foo("pqrs");
      Response response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.readEntity(Foo.class));

      // Invalid: Should have 1 each of field, property, class, and parameter violations,
      //          and no return value violations.
   	// BUT EXECUTABLE VALIDATION IS DISABLED. There will be no parameter violation.
      request = ClientBuilder.newClient().target("http://localhost:8080/Validation-test/rest/all/a/z").request();
      foo = new Foo("p");
      response = request.post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(400, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      String entity = response.readEntity(String.class);
      System.out.println("entity: " + entity);
      ResteasyViolationException e = new ResteasyViolationException(entity);
      countViolations(e, 3, 1, 1, 1, 0, 0);
      ResteasyConstraintViolation violation = e.getFieldViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("size must be between 2 and 4", violation.getMessage());
      Assert.assertEquals("a", violation.getValue());
      violation = e.getPropertyViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("size must be between 3 and 5", violation.getMessage());
      Assert.assertEquals("z", violation.getValue());
      violation = e.getClassViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("Concatenation of s and t must have length > 5", violation.getMessage());
      System.out.println("violation value: " + violation.getValue());
      Assert.assertTrue(violation.getValue().startsWith("org.jboss.resteasy.validation.TestResourceWithAllViolationTypes@"));
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
