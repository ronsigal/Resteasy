package org.jboss.resteasy.api.validation.logging;

import javax.validation.ValidatorFactory;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 12, 2013
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger
{
   LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());

   @LogMessage(level = Level.INFO)
   @Message(id = 3, value = "Using ValidatorFactory that supports CDI: %s")
   void supportingCDI(ValidatorFactory validatorFactory);

   @LogMessage(level = Level.INFO)
   @Message(id = 4, value = "Unable to find ValidatorFactory that supports CDI. Using default ValidatorFactory.")
   void unableToSupportCDI();
}
