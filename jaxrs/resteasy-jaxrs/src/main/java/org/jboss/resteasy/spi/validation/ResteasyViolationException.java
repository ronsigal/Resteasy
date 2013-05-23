package org.jboss.resteasy.spi.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

import org.jboss.resteasy.spi.validation.ValidationSupport.ClassViolation;
import org.jboss.resteasy.spi.validation.ValidationSupport.FieldViolation;
import org.jboss.resteasy.spi.validation.ValidationSupport.ParameterViolation;
import org.jboss.resteasy.spi.validation.ValidationSupport.PropertyViolation;
import org.jboss.resteasy.spi.validation.ValidationSupport.ReturnValueViolation;
import org.jboss.resteasy.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.validation.ViolationsContainer;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 * 
 * @TODO Need to work on representation of exceptions
 * @TODO Add javadoc.
 */
public class ResteasyViolationException extends ValidationException
{  
   private static final long serialVersionUID = 2623733139912277260L;
   
   private Exception exception;
   
   private List<ResteasyConstraintViolation> fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> classViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();
   
   private List<ResteasyConstraintViolation> allViolations; 
   private List<List<ResteasyConstraintViolation>> violationLists;
   
   public ResteasyViolationException(ViolationsContainer container)
   {
      convertToStrings(container);
      exception = container.getException();
   }
   
   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public List<ResteasyConstraintViolation> getViolations()
   {
      if (allViolations == null)
      {
         allViolations = new ArrayList<ResteasyConstraintViolation>();
         allViolations.addAll(fieldViolations);
         allViolations.addAll(propertyViolations);
         allViolations.addAll(classViolations);
         allViolations.addAll(parameterViolations);
         allViolations.addAll(returnValueViolations);
      }
      return allViolations;
   }
   
   public List<ResteasyConstraintViolation> getFieldViolations()
   {
      return fieldViolations;
   }
   
   public List<ResteasyConstraintViolation> getPropertyViolations()
   {
      return propertyViolations;
   }
   
   public List<ResteasyConstraintViolation> getClassViolations()
   {
      return classViolations;
   }
   
   public List<ResteasyConstraintViolation> getParameterViolations()
   {
      return parameterViolations;
   }
   
   public List<ResteasyConstraintViolation> getReturnValueViolations()
   {
      return returnValueViolations;
   }
   
   public int size()
   {
      return getViolations().size();
   }
   
   public List<List<ResteasyConstraintViolation>> getViolationLists()
   {
      return violationLists;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      for (Iterator<List<ResteasyConstraintViolation>> it = violationLists.iterator(); it.hasNext(); )
      {
         List<ResteasyConstraintViolation> violations = it.next();
         for (Iterator<ResteasyConstraintViolation> it2 = violations.iterator(); it2.hasNext(); )
         {
            sb.append(it2.next().toString()).append('\r');
         }
      }
      return sb.toString();
   }
   
   @SuppressWarnings("rawtypes")
   protected void convertToStrings(ViolationsContainer container)
   {
      if (violationLists != null)
      {
         return;
      }
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      Iterator it = container.getFieldViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         fieldViolations.add(new ValidationSupport.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      
      it = container.getPropertyViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         propertyViolations.add(new ValidationSupport.PropertyViolation(cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      
      it = container.getClassViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         classViolations.add(new ValidationSupport.ClassViolation(cv.getRootBeanClass().getName(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      
      it = container.getParameterViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         parameterViolations.add(new ValidationSupport.ParameterViolation(cv.getPropertyPath().toString(), cv.getMessage(), convertArrayToString(cv.getInvalidValue())));
      }
      
      it = container.getReturnValueViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         returnValueViolations.add(new ValidationSupport.ReturnValueViolation(cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue().toString()));
      }
      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }

   protected String convertArrayToString(Object o)
   {
      String result = null;
      if (o instanceof Object[])
      {
         Object[] array = Object[].class.cast(o);
         StringBuffer sb = new StringBuffer("[").append(convertArrayToString(array[0]));
         for (int i = 1; i < array.length; i++)
         {
            sb.append(", ").append(convertArrayToString(array[i]));
         }
         sb.append("]");
         result = sb.toString();
      }
      else
      {
         result = o.toString();
      }
      return result;
   }
}
