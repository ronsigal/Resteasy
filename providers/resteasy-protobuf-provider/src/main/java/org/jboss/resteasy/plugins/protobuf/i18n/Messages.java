package org.jboss.resteasy.plugins.protobuf.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 21000;

   @Message(id = BASE + 05, value = "Expected Message, got %s")
   String expectedMessage(Class<?> clazz);
}
