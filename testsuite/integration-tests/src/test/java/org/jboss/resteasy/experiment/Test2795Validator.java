package org.jboss.resteasy.experiment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class Test2795Validator implements ConstraintValidator<Test2795Constraint, String>
{
   public void initialize(Test2795Constraint constraintAnnotation) { }
   public boolean isValid(String value, ConstraintValidatorContext context) {
      return false;
   }
}
