package org.jboss.resteasy.test.async;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class MyResource
{
   @GET
   @Produces("text/plain")
   public void get(final @Suspended AsyncResponse response)
   {
      response.setTimeout(2000, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("STARTED!!!!");
               Thread.sleep(100);
               Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }

   @GET
   @Path("timeout")
   @Produces("text/plain")
   public void timeout(final @Suspended AsyncResponse response)
   {
      response.setTimeout(10, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               System.out.println("STARTED!!!!");
               Thread.sleep(100000);
               Response jaxrs = Response.ok("goodbye").type(MediaType.TEXT_PLAIN).build();
               response.resume(jaxrs);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };
      t.start();
   }
}