package org.jboss.resteasy.test.nextgen.validation;

import java.util.Locale;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 13, 2013
 */
public class ResourceServerI18N
{
   public static final String LOCALE_MARKER = "TestServerI18N current locale: ";
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   public static void main(String[] args)
   {
      try
      {
         System.out.println("Starting TestServerI18N");
         if (args.length == 0)
         {
            // do nothing
         }
         else if (args.length == 1)
         {
            Locale.setDefault(new Locale(args[0]));  
         }
         else if (args.length == 2)
         {
            Locale.setDefault(new Locale(args[0], args[1]));
         }
         else
         {
            Locale.setDefault(new Locale(args[0], args[1], args[2]));
         };
         System.out.println(LOCALE_MARKER + Locale.getDefault() + "|");
         before(TestResource.class);
         System.out.println("TestServerI18N started");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   public static void before(Class<?> resourceClass) throws Exception
   {
//      try
//      {
//         throw new EarlyReturnException("test");
//      }
//      catch (Exception e)
//      {
//         System.out.println("TestServerI18N.before() caught " + e);
//      }
      
      after();
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(resourceClass);
      System.out.println("added TestResource");
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   public void finalize() throws Exception
   {
      after();
   }
   
   @Path("")
   public static class TestResource
   {  
      @Path("ready")
      @GET
      public Response ready()
      {
         return Response.ok().build();
      }
      
      @Path("locale1/{language}")
      @GET
      public void locale1(@PathParam("language") String language)
      {
         Locale locale = new Locale(language);
         Locale.setDefault(locale);
         System.out.println("Set local to " + locale.toString());
      }
      
      @Path("locale2/{language}/{country}/")
      @GET
      public void locale2(@PathParam("language") String language, @PathParam("country") String country)
      {
         Locale locale = new Locale(language, country);
         Locale.setDefault(locale);
         System.out.println("Set local to " + locale.toString());
      }

      @Path("locale3/{language}/{country}/{variant}")
      @GET
      public void locale3(@PathParam("language") String language, @PathParam("country") String country, @PathParam("variant") String variant)
      {
         Locale locale = new Locale(language, country, variant);
         Locale.setDefault(locale);
         System.out.println("Set local to " + locale.toString());
      }
      
      @Path("printlocale")
      @GET
      public void printLocale()
      {
         System.out.println(LOCALE_MARKER + Locale.getDefault());
      }
      
      @Path("get")
      @GET
      @NotNull
      public String doGet()
      {
         return "abc";
      }
      
      @Path("violation")
      @GET
      @Null
      public String violation()
      {
         return "abc";
      }
   }
}