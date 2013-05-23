package org.jboss.resteasy.validation;

import java.io.Serializable;

abstract public class ResteasyConstraintViolation implements Serializable
{
   private static final long serialVersionUID = -5441628046215135260L;
   
   String path;
   String message;
   String value;
   
   public ResteasyConstraintViolation(String path, String message, String value)
   {
      this.path = path;
      this.message = message;
      this.value = value;
   }
   public String getPath()
   {
      return path;
   }
   public String getMessage()
   {
      return message;
   }
   public String getValue()
   {
      return value;
   }
   public String toString()
   {
      return path + "| " + message + "| " + value;
   }
   abstract public String type();
}