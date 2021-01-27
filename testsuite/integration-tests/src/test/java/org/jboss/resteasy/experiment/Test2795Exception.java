package org.jboss.resteasy.experiment;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class Test2795Exception extends ConstraintViolationException {
   private static final long serialVersionUID = 1L;

   public Test2795Exception(Set<? extends ConstraintViolation<?>> constraintViolations)
   {
      super(constraintViolations);
   }

   public Test2795Exception(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
      super(message, constraintViolations);
   }
}