package org.jboss.resteasy.experiment;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = Test2795Validator.class)
@Target({TYPE,METHOD})
@Retention(RUNTIME)
public @interface Test2795Constraint
{
   String message() default "custom";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
   String value();
}
