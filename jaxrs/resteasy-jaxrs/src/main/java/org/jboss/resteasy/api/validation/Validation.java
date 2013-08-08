package org.jboss.resteasy.api.validation;

import javax.ws.rs.core.MediaType;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 11, 2013
 */
public class Validation
{
   public static final String VALIDATION_HEADER = "validation-exception";
   public static final String VALIDATION_REPORT_XML = "application/vnd.redhat.validation+xml";
   public static final String VALIDATION_REPORT_JSON = "application/vnd.redhat.validation+json";
   public static final MediaType VALIDATION_REPORT_XML_TYPE = new MediaType("application", "vnd.redhat.validation+xml");
   public static final MediaType VALIDATION_REPORT_JSON_TYPE = new MediaType("application", "vnd.redhat.validation+json");
}
