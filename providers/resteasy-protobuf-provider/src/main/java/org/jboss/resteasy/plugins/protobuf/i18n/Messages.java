package org.jboss.resteasy.plugins.protobuf.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 21000;

   @Message(id = BASE + 05, value = "Expected Message, got %s")
   String expectedMessage(Class<?> clazz);
}
