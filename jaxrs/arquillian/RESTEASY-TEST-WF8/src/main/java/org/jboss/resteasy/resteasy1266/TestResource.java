package org.jboss.resteasy.resteasy1266;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

@Path("test")
public class TestResource
{
   private static final LogMessages log = LogMessages.LOGGER;
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";
   
   private @Context HttpHeaders headers;
   
   @GET
   @Path("return")
   public Response returnCookie()
   {
      Cookie oldCookie = headers.getCookies().get(OLD_COOKIE_NAME);
      log.info("server old cookie: " + oldCookie);
      Cookie cookie = headers.getCookies().get(NEW_COOKIE_NAME);
      log.info("server new cookie: " + cookie);
      return Response.status(cookie == null ? 444 : 200).build();
   }

   @GET
   @Path("get")
   public Response getCookie()
   {
      NewCookie cookie = new NewCookie(OLD_COOKIE_NAME, "value");
      return Response.ok().cookie(cookie).build();
   }
   
   @GET
   @Path("duplicate")
   public Response cookieParam(@CookieParam("cookie") Cookie cookie)
   {
      return Response.ok(cookie.getValue()).build();
   }
   
   @GET
   @Path("duplicates")
   public Response cookieParams(@CookieParam("cookie") Cookie[] cookies)
   {
      return Response.ok(cookies[0].getValue()).build();
   }
}