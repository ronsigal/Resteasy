package org.jboss.resteasy.resteasy767;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 28, 2013
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("sync")
   public Response sync()
   {
      return Response.ok().entity("sync").build();
   }
   
   @GET
   @Path("async/delay")
   public void asyncDelay(final @Suspended AsyncResponse response) throws Exception
   {
      response.setTimeout(10000, TimeUnit.MILLISECONDS);
      Thread t = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               Thread.sleep(5000);
               Response jaxrs = Response.ok("async/delay").build();
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
   @Path("async/nodelay")
   public void asyncNoDelay(final @Suspended AsyncResponse response) throws Exception
   {
      response.setTimeout(10000, TimeUnit.MILLISECONDS);
      Response jaxrs = Response.ok("async/nodelay").build();
      response.resume(jaxrs);
   }
   
}
