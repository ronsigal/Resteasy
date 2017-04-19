package org.jboss.resteasy.experiment.undertow;


import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 */
public class Test1617
{
   private static WeldContainer weldContainer;
   private static UndertowContainer undertowContainer;

   @ApplicationPath("")
   public static class TestApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         classes.add(ValidLicensePlateValidator.class);
         classes.add(VehicleRegistry.class);
         return classes;
      }
   }

   @Path("test")
   public static class TestResource {

      @POST
      @Path("")
      @Produces("text/plain")
      public Response license(@ValidLicensePlate String licensePlate) {
         return Response.ok("ok").build();
      }
   }

   @NotNull
   @Size(min = 2, max = 14)
   @Target({ METHOD, FIELD, PARAMETER, ANNOTATION_TYPE })
   @Retention(RUNTIME)
   @Constraint(validatedBy = ValidLicensePlateValidator.class)
   public @interface ValidLicensePlate {
      String message() default "bad plate";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
   }
   
   public static class VehicleRegistry {
      public boolean isValidLicensePlate(String licensePlate) {
         return licensePlate.startsWith("17");
      }
   }
   
   public static class ValidLicensePlateValidator
   implements ConstraintValidator<ValidLicensePlate, String> {

      public ValidLicensePlateValidator() {
         System.out.println("ValidLicensePlateValidator()");
      }
      @Inject
      private VehicleRegistry vehicleRegistry;

      @Override
      public void initialize(ValidLicensePlate constraintAnnotation) {
      }

      @Override
      public boolean isValid(String licensePlate, ConstraintValidatorContext constraintContext) {
         return vehicleRegistry.isValidLicensePlate( licensePlate );
      }
   }
   
   public static class UndertowContainer {
      private UndertowJaxrsServer server;
      
      public void run() {
         server = new UndertowJaxrsServer().start();
         ResteasyDeployment deployment = new ResteasyDeployment();
         deployment.setDeploymentSensitiveFactoryEnabled(true);
         deployment.setAddCharset(false);
         deployment.setApplication(new TestApp());
         deployment.start();
         server.deploy(deployment);
      }
      
      public void shutdown() {
         server.stop();
      }
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void before() throws Exception
   {
      weldContainer = new Weld().disableDiscovery().addPackages(Test1617.class.getPackage()).initialize();
      undertowContainer = weldContainer.select(UndertowContainer.class).get();
      undertowContainer.run();
   }

   @AfterClass
   public static void after() throws Exception
   {
      if (undertowContainer != null) {
         undertowContainer.shutdown();
      }
      if (weldContainer != null) {
         weldContainer.shutdown();
      }
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testInjection() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8081/test").request();
      Response response = request.post(Entity.entity("19x", "text/plain"));
      Assert.assertEquals(400, response.getStatus());
      ViolationReport report = response.readEntity(ViolationReport.class);
      Assert.assertTrue(report.getParameterViolations().size() > 0);
      client.close();
   }
}