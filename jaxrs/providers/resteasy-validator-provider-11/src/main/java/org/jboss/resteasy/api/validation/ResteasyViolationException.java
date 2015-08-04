package org.jboss.resteasy.api.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.plugins.validation.ConstraintTypeUtil11;
import org.jboss.resteasy.plugins.validation.GeneralValidatorImpl;
import org.jboss.resteasy.plugins.validation.SimpleViolationsContainer;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 * 
 * @TODO Need to work on representation of exceptions
 * @TODO Add javadoc.
 */
public class ResteasyViolationException extends ConstraintViolationException
{  
   private static final long serialVersionUID = 2623733139912277260L;
   
   private List<MediaType> accept;
   private Exception exception;
   
   private List<ResteasyConstraintViolation> fieldViolations;
   private List<ResteasyConstraintViolation> propertyViolations;
   private List<ResteasyConstraintViolation> classViolations;
   private List<ResteasyConstraintViolation> parameterViolations;
   private List<ResteasyConstraintViolation> returnValueViolations;
   
   private List<ResteasyConstraintViolation> allViolations; 
   private List<List<ResteasyConstraintViolation>> violationLists;
   
   private ConstraintTypeUtil11 util = new ConstraintTypeUtil11();
   private boolean suppressPath;
   
   /**
    * New constructor
    * @param constraintViolations
    */
   public ResteasyViolationException(Set<? extends ConstraintViolation<?>> constraintViolations)
   {
      super(constraintViolations);
      checkSuppressPath();
      accept = new ArrayList<MediaType>();
      accept.add(MediaType.TEXT_PLAIN_TYPE);
   }
   
   /**
    * New constructor
    * 
    * @param constraintViolations
    * @param accept
    */
   public ResteasyViolationException(Set<? extends ConstraintViolation<?>> constraintViolations, List<MediaType> accept)
   {
      super(constraintViolations);
      checkSuppressPath();
      this.accept = accept;
   }
   
   /**
    * New constructor
    * 
    * @param container
    */
   public ResteasyViolationException(SimpleViolationsContainer container)
   {
      this(container.getViolations());
      exception = container.getException();
   }
   
   /**
    * New constructor
    * 
    * @param container
    * @param accept
    */
   
   public ResteasyViolationException(SimpleViolationsContainer container, List<MediaType> accept)
   {
      this(container.getViolations(), accept);
      exception = container.getException();
   }
   
   public ResteasyViolationException(ViolationsContainer<?> container)
   {
      super(null);
      convertToStrings(container);
      exception = container.getException();
      accept = new ArrayList<MediaType>();
      accept.add(MediaType.TEXT_PLAIN_TYPE);
   }
   
   public ResteasyViolationException(ViolationsContainer<?> container, List<MediaType> accept)
   {
      super(null);
      convertToStrings(container);
      exception = container.getException();
      this.accept = accept;
   }
   
   public ResteasyViolationException(String stringRep)
   {
      super(null);
      checkSuppressPath();
      convertFromString(stringRep);
   }
   
   public List<MediaType> getAccept()
   {
      return accept;
   }

   public void setAccept(List<MediaType> accept)
   {
      this.accept = accept;
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
      convertViolations();
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
      convertViolations();
      return fieldViolations;
   }
   
   public List<ResteasyConstraintViolation> getPropertyViolations()
   {
      convertViolations();
      return propertyViolations;
   }
   
   public List<ResteasyConstraintViolation> getClassViolations()
   {
      convertViolations();
      return classViolations;
   }
   
   public List<ResteasyConstraintViolation> getParameterViolations()
   {
      convertViolations();
      return parameterViolations;
   }
   
   public List<ResteasyConstraintViolation> getReturnValueViolations()
   {
      convertViolations();
      return returnValueViolations;
   }
   
   public int size()
   {
      return getViolations().size();
   }
   
   public List<List<ResteasyConstraintViolation>> getViolationLists()
   {
      convertViolations();
      return violationLists;
   }
   
   public String toString()
   {
      convertViolations();
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
   
   protected void convertToStrings(ViolationsContainer<?> container)
   {
      if (violationLists != null)
      {
         return;
      }
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      fieldViolations = container.getFieldViolations();
      propertyViolations = container.getPropertyViolations();
      classViolations = container.getClassViolations();
      parameterViolations = container.getParameterViolations();
      returnValueViolations = container.getReturnValueViolations();

      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }
   
   protected void convertFromString(String stringRep)
   {
      convertViolations();
      InputStream is = new ByteArrayInputStream(stringRep.getBytes());
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line;
      try
      {
         line = br.readLine();
         while (line != null )
         {
            ConstraintType.Type type = ConstraintType.Type.valueOf(line.substring(1, line.length() - 1));
            line = br.readLine();
            String path = line.substring(1, line.length() - 1);
            line = br.readLine();
            String message = line.substring(1, line.length() - 1);
            line = br.readLine();
            String value = line.substring(1, line.length() - 1);
            ResteasyConstraintViolation rcv = new ResteasyConstraintViolation(type, path, message, value);
            
            switch (type)
            {
               case FIELD:
                  fieldViolations.add(rcv);
                  break;
                  
               case PROPERTY:
                  propertyViolations.add(rcv);
                  break;
                  
               case CLASS:
                  classViolations.add(rcv);
                  break;
                  
               case PARAMETER:
                  parameterViolations.add(rcv);
                  break;
                  
               case RETURN_VALUE:
                  returnValueViolations.add(rcv);
                  break;
                  
               default:
                  throw new RuntimeException("unexpected violation type: " + type);
            }
            line = br.readLine(); // consume ending '\r'
            line = br.readLine();
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to parse ResteasyViolationException");
      }
      
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }
   
   protected int getField(int start, String line)
   {
      int beginning = line.indexOf('[', start);
      if (beginning == -1)
      {
         throw new RuntimeException("ResteasyViolationException has invalid format: " + line);
      }
      int index = beginning;
      int bracketCount = 1;
      while (++index < line.length())
      {
         char c = line.charAt(index);
         if (c == '[')
         {
            bracketCount++;
         }
         else if (c == ']')
         {
            bracketCount--;
         }
         if (bracketCount == 0)
         {
            break;
         }
      }
      if (bracketCount != 0)
      {
         throw new RuntimeException("ResteasyViolationException has invalid format: " + line);
      }
      return index;
   }
   
   protected void checkSuppressPath()
   {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null)
      {
         String s = context.getParameter(GeneralValidatorImpl.SUPPRESS_VIOLATION_PATH);
         if (s != null)
         {
            suppressPath = Boolean.parseBoolean(s);
         }
      }
   }
   
   protected void convertViolations()
   {
      if (violationLists != null)
      {
         return;
      }
      
      fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
      propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
      classViolations       = new ArrayList<ResteasyConstraintViolation>();
      parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
      returnValueViolations = new ArrayList<ResteasyConstraintViolation>();
      
      if (getConstraintViolations() != null)
      {
         for (Iterator<ConstraintViolation<?>> it = getConstraintViolations().iterator(); it.hasNext(); )
         {
            ResteasyConstraintViolation rcv = convertViolation(it.next());
            switch (rcv.getConstraintType())
            {
               case FIELD:
                  fieldViolations.add(rcv);
                  break;

               case PROPERTY:
                  propertyViolations.add(rcv);
                  break;

               case CLASS:
                  classViolations.add(rcv);
                  break;

               case PARAMETER:
                  parameterViolations.add(rcv);
                  break;

               case RETURN_VALUE:
                  returnValueViolations.add(rcv);
                  break;

               default:
                  throw new RuntimeException("unexpected violation type: " + rcv.getConstraintType());
            }
         }
      }
      
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }
   
   protected ResteasyConstraintViolation convertViolation(ConstraintViolation<?> violation)
   {
      Type ct = util.getConstraintType(violation);
      String path = (suppressPath ? "*" : violation.getPropertyPath().toString());
      return new ResteasyConstraintViolation(ct, path, violation.getMessage(), convertArrayToString(violation.getInvalidValue()));
   }
   
   static protected String convertArrayToString(Object o)
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
         result = (o == null ? "" : o.toString());
      }
      return result;
   }
}
