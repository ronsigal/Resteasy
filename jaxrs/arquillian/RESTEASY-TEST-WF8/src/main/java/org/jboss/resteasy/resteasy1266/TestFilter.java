package org.jboss.resteasy.resteasy1266;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

@Provider
@PreMatching
public class TestFilter implements ContainerRequestFilter
{
   private static final LogMessages log = LogMessages.LOGGER;
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      log.info("entering TestFilter.filter()");
      final Cookie cookie = requestContext.getCookies().get(OLD_COOKIE_NAME);
      log.info("cookie: " + cookie);
      if (cookie != null)
//      if (cookie != null && requestContext.getCookies().containsKey(NEW_COOKIE_NAME))
      {//requestContext.getHeaders().addFirst("x", "y");
         requestContext.getHeaders().add(HttpHeaders.COOKIE, new Cookie(NEW_COOKIE_NAME, cookie.getValue()).toString());
      }
   }
}