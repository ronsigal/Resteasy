package org.jboss.resteasy.api.validation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright July 27, 2013
 */
@XmlRootElement(name="violationReport")
//@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class ViolationReport
{
   private String exception;
   
//   @XmlElement 
   @XmlElement(name = "fieldViolations", type = ResteasyConstraintViolation.class)
   private ArrayList<ResteasyConstraintViolation> fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
//   @XmlElement 
//   @XmlElement(name = "propertyViolations", type = ResteasyConstraintViolation.class)
   private ArrayList<ResteasyConstraintViolation> propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
//   @XmlElement 
//   @XmlElement(name = "classViolations", type = ResteasyConstraintViolation.class)
   private ArrayList<ResteasyConstraintViolation> classViolations       = new ArrayList<ResteasyConstraintViolation>();
//   @XmlElement 
//   @XmlElement(name = "parameterViolations", type = ResteasyConstraintViolation.class)
   private ArrayList<ResteasyConstraintViolation> parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
//   @XmlElement 
//   @XmlElement(name = "returnValueViolations", type = ResteasyConstraintViolation.class)
   private ArrayList<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();
   
//   @XmlElement 
//   @XmlElement(name = "allViolations", type = ResteasyConstraintViolation.class)
//   private ArrayList<ResteasyConstraintViolation> allViolations; 
//   @XmlElement 
//   @XmlElement(name = "fieldViolations", type = ResteasyConstraintViolation.class)
//   private ArrayList<List<ResteasyConstraintViolation>> violationLists;
   
   public ViolationReport(ResteasyViolationException exception)
   {
      Exception e = exception.getException();
      if (e != null)
      {
         this.exception = e.toString();
      }
      this.fieldViolations = (ArrayList<ResteasyConstraintViolation>) exception.getFieldViolations();
      this.propertyViolations = (ArrayList<ResteasyConstraintViolation>) exception.getPropertyViolations();
      this.classViolations = (ArrayList<ResteasyConstraintViolation>) exception.getClassViolations();
      this.parameterViolations = (ArrayList<ResteasyConstraintViolation>) exception.getParameterViolations();
      this.returnValueViolations = (ArrayList<ResteasyConstraintViolation>) exception.getReturnValueViolations();
//      this.allViolations = (ArrayList<ResteasyConstraintViolation>) exception.getViolations();
//      this.violationLists = (ArrayList<List<ResteasyConstraintViolation>>) exception.getViolationLists();
   }
   
   public ViolationReport()
   {
   }

//   @XmlElement
   public String getException()
   {
      return exception;
   }

//   @XmlElement
   public ArrayList<ResteasyConstraintViolation> getFieldViolations()
   {
      return fieldViolations;
   }

//   @XmlElement
   public ArrayList<ResteasyConstraintViolation> getPropertyViolations()
   {
      return propertyViolations;
   }

//   @XmlElement
   public ArrayList<ResteasyConstraintViolation> getClassViolations()
   {
      return classViolations;
   }

//   @XmlElement
   public ArrayList<ResteasyConstraintViolation> getParameterViolations()
   {
      return parameterViolations;
   }

//   @XmlElement
   public ArrayList<ResteasyConstraintViolation> getReturnValueViolations()
   {
      return returnValueViolations;
   }

//   @XmlElement
//   public ArrayList<ResteasyConstraintViolation> getAllViolations()
//   {
//      return allViolations;
//   }

   public void setException(String exception)
   {
      this.exception = exception;
   }

   public void setFieldViolations(
         ArrayList<ResteasyConstraintViolation> fieldViolations)
   {
      this.fieldViolations = fieldViolations;
   }

   public void setPropertyViolations(
         ArrayList<ResteasyConstraintViolation> propertyViolations)
   {
      this.propertyViolations = propertyViolations;
   }

   public void setClassViolations(
         ArrayList<ResteasyConstraintViolation> classViolations)
   {
      this.classViolations = classViolations;
   }

   public void setParameterViolations(
         ArrayList<ResteasyConstraintViolation> parameterViolations)
   {
      this.parameterViolations = parameterViolations;
   }

   public void setReturnValueViolations(
         ArrayList<ResteasyConstraintViolation> returnValueViolations)
   {
      this.returnValueViolations = returnValueViolations;
   }

//   public void setAllViolations(
//         ArrayList<ResteasyConstraintViolation> allViolations)
//   {
//      this.allViolations = allViolations;
//   }

//   public void setViolationLists(
//         ArrayList<List<ResteasyConstraintViolation>> violationLists)
//   {
//      this.violationLists = violationLists;
//   }

////   @XmlElement
//   public List<List<ResteasyConstraintViolation>> getViolationLists()
//   {
//      return violationLists;
//   }
   
//   @XmlElement(name = "link")
//   public List<Link> getLinks()
//   {
//      return links;
//   }
}
