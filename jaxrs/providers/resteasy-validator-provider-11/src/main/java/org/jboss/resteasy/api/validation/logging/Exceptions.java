package org.jboss.resteasy.api.validation.logging;

import javax.validation.ElementKind;
import javax.validation.ValidationException;

import org.jboss.logging.Cause;
import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;
import org.jboss.logging.Messages;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 26, 2013
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Exceptions
{
   Exceptions EXCEPTIONS = Messages.getBundle(Exceptions.class);
   
   @Message(id = 7, value = "Unexpected violation type: %s")
   RuntimeException unexpectedViolationType(String type);
   
   @Message(id = 8, value = "Unable to parse ResteasyViolationException")
   RuntimeException unableToParseResteasyViolationException();
   
   @Message(id = 10, value = "Unknown object passed as constraint violation: %s")
   RuntimeException unknownObjectPassedAsConstraintViolation(Object o);
   
   @Message(id = 11, value = "Unexpected path node type in method violation: %s")
   RuntimeException unexpectedPathNodeTypeInMethodViolation(Object kind);

   @Message(id = 12, value = "Unexpected path node type: %s")
   RuntimeException unexpectedPathNodeType(ElementKind kind);
   
   @Message(id = 13, value = "Unable to load Validation support")
   ValidationException unableToLoadValidationSupport(@Cause Exception e);
   
   @Message(id = 14, value = "Expect two non-null methods")
   RuntimeException expectTwoNonNullMethods();
   
}
