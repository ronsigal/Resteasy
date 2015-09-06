package org.jboss.resteasy.resteasy1186c;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("async")
public interface AsyncRootResource extends AsyncValidResource {
   
   @GET
   @Override
   void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);
   
   @Path("/sub")
   AsyncSubResource getSubResource();
   
}
