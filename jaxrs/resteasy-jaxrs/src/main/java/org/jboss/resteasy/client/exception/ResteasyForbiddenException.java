package org.jboss.resteasy.client.exception;

import javax.ws.rs.ForbiddenException;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 23, 2016
 */
public class ResteasyForbiddenException extends ForbiddenException implements DebugLevelException
{
   private static final long serialVersionUID = -4760979083941340629L;
}
