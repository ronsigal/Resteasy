package org.jboss.resteasy.test.validation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 16, 2012
 */
public class TestValidationXML
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @FooConstraint(min=1,max=3)
   public static class Foo implements Serializable
   {
      private static final long serialVersionUID = -1068336400309384949L;
      private String s;

      public Foo(String s)
      {
         this.s = s;
      }
      public String toString()
      {
         return "Foo[" + s + "]";
      }
      public boolean equals(Object o)
      {
         if (o == null || !(o instanceof Foo))
         {
            return false;
         }
         return this.s.equals(Foo.class.cast(o).s);
      }
   }

   public static class FooValidator implements ConstraintValidator<FooConstraint, Foo>
   {
      int min;
      int max;

      public void initialize(FooConstraint constraintAnnotation)
      {
         min = constraintAnnotation.min();
         max = constraintAnnotation.max();
      }
      public boolean isValid(Foo value, ConstraintValidatorContext context)
      {
         return min <= value.s.length() && value.s.length() <= max;
      }
   }

   @Documented
   @Constraint(validatedBy = FooValidator.class)
   @Target({TYPE,PARAMETER,METHOD})
   @Retention(RUNTIME)
   public @interface FooConstraint {
      String message() default "s must have length: {min} <= length <= {max}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int min();
      int max();
   }

   @Provider
   @Produces("application/foo")
   @Consumes("application/foo")
   public static class FooReaderWriter implements MessageBodyReader<Foo>, MessageBodyWriter<Foo>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Foo.class.equals(type);
      }
      public long getSize(Foo t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }
      public void writeTo(Foo t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException
      {
    	  byte[] b = t.s.getBytes();
    	  entityStream.write(b.length);
    	  entityStream.write(t.s.getBytes());
    	  entityStream.flush();
      }
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Foo.class.equals(type);
      }
      public Foo readFrom(Class<Foo> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
    	  int length = entityStream.read();
    	  byte[] b = new byte[length]; 
    	  entityStream.read(b);
    	  String s = new String(b);
    	  return new Foo(s);
      }
   }

   @Path("/")
   public static class TestResourceWithReturnValues
   {  
      @POST
      @Path("/native")
      @Valid
      public Foo postNative(Foo foo)
      {
         return foo;
      }

      @POST
      @Path("/imposed")
      @FooConstraint(min=3,max=5)
      public Foo postImposed(Foo foo)
      {
         return foo;
      }
      
      @POST
      @Path("nativeAndImposed")
      @Valid
      @FooConstraint(min=3,max=5)
      public Foo postNativeAndImposed(Foo foo)
      {
         return foo;
      }
   }
   
   public static class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResourceWithAllFivePotentialViolations>
   {
      int length;

      public void initialize(TestClassConstraint constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(TestResourceWithAllFivePotentialViolations value, ConstraintValidatorContext context)
      {
         return value.s.length() + value.u.length() >= length;
      }

   }

   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassConstraint {
      String message() default "Concatenation of s and u must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }
   
   @Path("/{s}/{t}/{u}")
   @TestClassConstraint(5)
   public static class TestResourceWithAllFivePotentialViolations
   {
      @Size(min=2, max=4)
      @PathParam("s")
      private String s;

      @Size(min=2, max=4)
      @PathParam("t")
      private String t;
      
      private String u;

      @Size(min=3, max=5)  
      public String getU()
      {
         return u;
      }

      @PathParam("u") 
      public void setU(String u)
      {
         this.u = u;
      }

      @POST
      @Path("{unused}/{unused}")
      @FooConstraint(min=4,max=5)
      public Foo post( @FooConstraint(min=3,max=5) Foo foo)
      {
         return foo;
      }
   }

   public static void before(Class<?> resourceClass) throws Exception
   {
      after();
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(resourceClass);
   }
   
   public static void beforeFoo(Class<?> resourceClass) throws Exception
   {
      before(resourceClass);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

//   @Test
   public void testReturnValues() throws Exception
   {
      beforeFoo(TestResourceWithReturnValues.class);

//      {
//         // Valid native constraint
//         ClientRequest request = new ClientRequest(generateURL("/native"));
//         Foo foo = new Foo("a");
//         request.body("application/foo", foo);
//         ClientResponse<?> response = request.post(Foo.class);     
//         Assert.assertEquals(200, response.getStatus());
//         Assert.assertEquals(foo, response.getEntity());
//      }
//      
//      {
//         // Valid imposed constraint
//         ClientRequest request = new ClientRequest(generateURL("/imposed"));
//         Foo foo = new Foo("abcde");
//         request.body("application/foo", foo);
//         ClientResponse<?> response = request.post(Foo.class);      
//         Assert.assertEquals(200, response.getStatus());
//         Assert.assertEquals(foo, response.getEntity());
//      }
//      
//      {
//         // Valid native and imposed constraints.
//         ClientRequest request = new ClientRequest(generateURL("/nativeAndImposed"));
//         Foo foo = new Foo("abc");
//         request.body("application/foo", foo);
//         ClientResponse<?> response = request.post(Foo.class);      
//         Assert.assertEquals(200, response.getStatus());
//         Assert.assertEquals(foo, response.getEntity());
//      }
      
//      {
//         // Invalid native constraint
//         ClientRequest request = new ClientRequest(generateURL("/native"));
//         request.body("application/foo", new Foo("abcdef"));
//         request.accept(MediaType.APPLICATION_XML);
//         ClientResponse<?> response = request.post(Foo.class);
//         Assert.assertEquals(500, response.getStatus());
//         Object entity = response.getEntity(String.class);
//         System.out.println("entity: " + entity);
//         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
//         countViolations(e, 1, 0, 0, 0, 0, 1);
//         ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
//         Assert.assertTrue(cv.getMessage().equals("s must have length: 1 <= length <= 3"));
//         Assert.assertEquals("Foo[abcdef]", cv.getValue());
//      }
      
//      {
//         // Invalid imposed constraint
//         ClientRequest request = new ClientRequest(generateURL("/imposed"));
//         request.body("application/foo", new Foo("abcdef"));
//         ClientResponse<?> response = request.post(Foo.class);
//         Assert.assertEquals(500, response.getStatus());
//         Object entity = response.getEntity(String.class);
//         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
//         countViolations(e, 1, 0, 0, 0, 0, 1);
//         ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
//         Assert.assertTrue(cv.getMessage().equals("s must have length: 3 <= length <= 5"));
//         Assert.assertEquals("Foo[abcdef]", cv.getValue());
//      }
//      
//      {
//         // Invalid native and imposed constraints
//         ClientRequest request = new ClientRequest(generateURL("/nativeAndImposed"));
//         request.body("application/foo", new Foo("abcdef"));
//         ClientResponse<?> response = request.post(Foo.class); 
//         Assert.assertEquals(500, response.getStatus());
//         Object entity = response.getEntity(String.class);
//         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
//         countViolations(e, 2, 0, 0, 0, 0, 2);
//         Iterator<ResteasyConstraintViolation> it = e.getReturnValueViolations().iterator(); 
//         ResteasyConstraintViolation cv1 = it.next();
//         ResteasyConstraintViolation cv2 = it.next();
//         if (cv1.toString().indexOf('1') < 0)
//         {
//            ResteasyConstraintViolation temp = cv1;
//            cv1 = cv2;
//            cv2 = temp;
//         }
//         Assert.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"));
//         Assert.assertEquals("Foo[abcdef]", cv1.getValue());
//         Assert.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"));
//         Assert.assertEquals("Foo[abcdef]", cv2.getValue());
//      }
      
      after();
   }

   @Test
   public void testViolationsBeforeReturnValue() throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

//      {
//         // Valid
//         ClientRequest request = new ClientRequest(generateURL("/abc/wxyz/unused/unused"));
//         Foo foo = new Foo("pqrs");
//         request.body("application/foo", foo);
//         ClientResponse<?> response = request.post(Foo.class);     
//         Assert.assertEquals(200, response.getStatus());
//         Assert.assertEquals(foo, response.getEntity());
//      }
//
      {
         // Invalid: Should have 1 each of field, property, class, and parameter violations,
         //          and no return value violations.
         ClientRequest request = new ClientRequest(generateURL("/a/b/c/unused/unused"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("report: " + entity);
         String expected = "violationReport><fieldViolations><constraintType>FIELD</constraintType><path>s</path><message>size must be between 2 and 4</message><value>a</value></fieldViolations><fieldViolations><constraintType>FIELD</constraintType><path>t</path><message>size must be between 2 and 4</message><value>b</value></fieldViolations><propertyViolations><constraintType>PROPERTY</constraintType><path>u</path><message>size must be between 3 and 5</message><value>c</value></propertyViolations><classViolations><constraintType>CLASS</constraintType><path></path><message>Concatenation of s and t must have length &gt; 5</message><value>org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations@5948221d</value></classViolations><parameterViolations><constraintType>PARAMETER</constraintType><path>post.arg0</path><message>s must have length: 3 &lt;= length &lt;= 5</message><value>Foo[p]</value></parameterViolations></violationReport>";
         Assert.assertTrue(entity.contains(entity));
      }
//      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
//      countViolations(e, 4, 1, 1, 1, 1, 0);
//      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
//      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
//      Assert.assertEquals("a", cv.getValue());
//      cv = e.getPropertyViolations().iterator().next();
//      Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
//      Assert.assertEquals("z", cv.getValue());
//      cv = e.getClassViolations().iterator().next();
//      Assert.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage());
//      Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
//      cv = e.getParameterViolations().iterator().next();
//      Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
//      Assert.assertEquals("Foo[p]", cv.getValue());
      
      {
         // Invalid: Should have 1 each of field, property, class, and parameter violations,
         //          and no return value violations.
         ClientRequest request = new ClientRequest(generateURL("/a/b/c/unused/unused"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         ViolationReport report = response.getEntity(ViolationReport.class);
         System.out.println("report: " + report);
         countViolations(report, 4, 2, 1, 1, 1, 0);
         Iterator<ResteasyConstraintViolation> iterator = report.getFieldViolations().iterator();
         ResteasyConstraintViolation cv1 = iterator.next();
         ResteasyConstraintViolation cv2 = iterator.next();
         if (!("a").equals(cv1.getValue()))
         {
            ResteasyConstraintViolation tmp = cv1;
            cv1 = cv2;
            cv2 = tmp;
         }
         Assert.assertEquals("size must be between 2 and 4", cv1.getMessage());
         Assert.assertEquals("a", cv1.getValue());
         Assert.assertEquals("size must be between 2 and 4", cv2.getMessage());
         Assert.assertEquals("b", cv2.getValue());
         ResteasyConstraintViolation cv = report.getPropertyViolations().iterator().next();
         Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
         Assert.assertEquals("c", cv.getValue());
         cv = report.getClassViolations().iterator().next();
         Assert.assertEquals("Concatenation of s and u must have length > 5", cv.getMessage());
         System.out.print("value: " + cv.getValue());
         Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations@"));
         cv = report.getParameterViolations().iterator().next();
         Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
         Assert.assertEquals("Foo[p]", cv.getValue());
      }
      
      after();
   }
   
   private void countViolations(ViolationReport e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
//      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
   
   private List<String> getMessages(List<ResteasyConstraintViolation> rcvs)
   {
      List<String> list = new ArrayList<String>();
      for (Iterator<ResteasyConstraintViolation> it = rcvs.iterator(); it.hasNext(); )
      {
         ResteasyConstraintViolation rcv = it.next();
         list.add(rcv.getMessage() + "; " + rcv.getValue());
      }
      return list;
   }
}
