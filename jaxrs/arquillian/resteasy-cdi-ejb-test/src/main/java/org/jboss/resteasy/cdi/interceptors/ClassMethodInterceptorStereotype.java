package org.jboss.resteasy.cdi.interceptors;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 4, 2012
 */
@MethodBinding
@ClassInterceptorStereotype
@Stereotype
@Target(TYPE)
@Retention(RUNTIME)
public @interface ClassMethodInterceptorStereotype
{
}
