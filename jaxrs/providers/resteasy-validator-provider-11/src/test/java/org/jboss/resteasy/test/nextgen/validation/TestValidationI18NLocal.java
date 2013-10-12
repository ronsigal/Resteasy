package org.jboss.resteasy.test.nextgen.validation;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;

import javax.lang.model.element.ElementKind;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.executable.ExecutableType;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.jboss.logging.Messages;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.logging.Exceptions;
import org.jboss.resteasy.plugins.validation.ConstraintTypeUtil11;
import org.jboss.resteasy.plugins.validation.GeneralValidatorImpl;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for RESTEASY-937
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 2, 2013
 * 
 * @version $Revision: 1 $
 */
@RunWith(BMUnitRunner.class)
abstract public class TestValidationI18NLocal
{
   abstract Locale getLocale();

   @Test
   public void resteasyViolationException_convertFromString_badEnum() throws Exception
   {
      System.out.println("running resteasyViolationException_convertFromString_badEnum");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         new ResteasyViolationException("|abc|");
      }
      catch (RuntimeException e)
      {
         String expected = Messages.getBundle(Exceptions.class, getLocale()).unexpectedViolationType("abc").getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(expected, e.getMessage());
      }
      catch (Throwable t)
      {
         Assert.fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unexpectedViolationType("abc").getLocalizedMessage() + ")");
      }
   }

   @BMRule(name="ResteasyViolationException.convertFromString_unableToParseResteasyViolationException",
         targetClass="BufferedReader",
         targetMethod="readLine",
         condition="callerEquals(\"ResteasyViolationException.convertFromString\", true)",
         action="traceln(\"bm: BufferedReader.readLine() throwing IOException\"); throw new IOException();")
   @Test
   public void resteasyViolationException_convertFromString_unableToParseResteasyViolationException() throws Exception
   {
      System.out.println("running resteasyViolationException_convertFromString_unableToParseResteasyViolationException()");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         new ResteasyViolationException("abc");
      }
      catch (RuntimeException e)
      {
         String expected = Messages.getBundle(Exceptions.class, getLocale()).unableToParseResteasyViolationException().getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(expected, e.getMessage());
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         Assert.fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unableToParseResteasyViolationException().getLocalizedMessage() + ")");
      }
   }

   @Test
   public void resteasyViolationException_unknownConstraintObject() throws Exception
   {
      System.out.println("running resteasyViolationException_unknownConstraintObject");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         new ConstraintTypeUtil11().getConstraintType("bad");
      }
      catch (RuntimeException e)
      {
         String expected = Messages.getBundle(Exceptions.class, getLocale()).unknownObjectPassedAsConstraintViolation("bad").getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(expected, e.getMessage());
      }
      catch (Throwable t)
      {
         Assert.fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unexpectedViolationType("abc").getLocalizedMessage() + ")");
      }
   }

   @BMRule(name="constraintTypeUtil11_getConstraintType_unexpectedPathNode",
         targetClass="javax.validation.Path$Node",
         isInterface=true,
         isOverriding=true,
         targetMethod="getKind",
         targetLocation="AT ENTRY",
         action="traceln(\"returning METHOD\"); RETURN javax.validation.ElementKind.METHOD;")
   @Test
   public void constraintTypeUtil11_getConstraintType_unexpectedPathNode() throws Exception
   {
      System.out.println("running constraintTypeUtil11_getConstraintType_unexpectedPathNode");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         ConstraintViolation<?> cv = new ConstraintViolation<Object>()
         {
            public String getMessage()
            {
               return null;
            }
            public String getMessageTemplate()
            {
               return null;
            }
            public Object getRootBean()
            {
               return null;
            }
            public Class<Object> getRootBeanClass()
            {
               return null;
            }
            public Object getLeafBean()
            {
               return null;
            }
            public Object[] getExecutableParameters()
            {
               return null;
            }
            public Object getExecutableReturnValue()
            {
               return null;
            }
            public Path getPropertyPath()
            {
               return PathImpl.createPathFromString("a[b]");
            }
            public Object getInvalidValue()
            {
               return null;
            }
            public ConstraintDescriptor<?> getConstraintDescriptor()
            {
               return null;
            }
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Object unwrap(Class type)
            {
               return null;
            }
         };
         new ConstraintTypeUtil11().getConstraintType(cv);
         fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unexpectedPathNodeTypeInMethodViolation(17).getLocalizedMessage() + ")");
      }
      catch (RuntimeException e)
      {
         String expected = Messages.getBundle(Exceptions.class, getLocale()).unexpectedPathNodeTypeInMethodViolation(ElementKind.METHOD).getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(expected, e.getMessage());
      }
      catch (AssertionError e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         Assert.fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unexpectedPathNodeTypeInMethodViolation(17).getLocalizedMessage() + "): " + " got " + t.getMessage());
      }
   }

   @BMRule(name="validatorContextResolver_getContext_unableToLoadValidationSupport",
         targetClass="javax.validation.bootstrap.GenericBootstrap",
         isInterface=true,
         isOverriding=true,
         targetMethod="configure",
         action="THROW new RuntimeException(\"bm\");")
   @Test
   public void validatorContextResolver_getContext_unableToLoadValidationSupport() throws Exception
   {
      System.out.println("running validatorContextResolver_getContext_unableToLoadValidationSupport()");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         new ValidatorContextResolver().getContext(null);
         fail();
      }
      catch (RuntimeException e)
      {
         String expected = Messages.getBundle(Exceptions.class, getLocale()).unableToLoadValidationSupport(new RuntimeException("bm")).getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(expected, e.getMessage());
      }
      catch (AssertionError e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         Assert.fail("expected RuntimeException(" + Exceptions.EXCEPTIONS.unableToParseResteasyViolationException().getLocalizedMessage() + ")");
      }
   }

   @Test
   public void generalValidatorImpl_overrides_expectTwoNonNullMethods() throws Exception
   {
      System.out.println("running generalValidatorImpl_overrides_expectTwoNonNullMethods()");
      Locale.setDefault(getLocale());
      System.out.println("locale: " + Locale.getDefault());
      try
      {
         GeneralValidatorImpl validator = new GeneralValidatorImpl(null, false, new HashSet<ExecutableType>());
         Method method = GeneralValidatorImpl.class.getDeclaredMethod("overrides", Method.class, Method.class);
         method.setAccessible(true);
         method.invoke(validator, null, null);
         fail("fail: expected " + Messages.getBundle(Exceptions.class, getLocale()).expectTwoNonNullMethods().getMessage());
      }
      catch (AssertionError e)
      {
         throw e;
      }
      catch (Throwable t)
      {
         Throwable t1 = t.getCause();
         Assert.assertTrue("expected RuntimeException", t1 instanceof RuntimeException);
         RuntimeException e = RuntimeException.class.cast(t1);
         String expected = Messages.getBundle(Exceptions.class, getLocale()).expectTwoNonNullMethods().getMessage();
         System.out.println("expected message: " + expected);
         System.out.println("actual message:   " + e.getMessage());
         Assert.assertEquals(Exceptions.EXCEPTIONS.unableToParseResteasyViolationException().getLocalizedMessage() + ")", expected, e.getMessage());
      }
   }
}
