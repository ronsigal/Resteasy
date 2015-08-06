package org.jboss.resteasy.resteasy1186;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("async")
public interface AsyncRootResource {
   
   @Path("/sub")
   AsyncSubResource getSubResource();
   
   @Path("entered")
   @GET
   public Response entered();
   
}
