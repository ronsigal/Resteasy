package org.jboss.resteasy.resteasy1186;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

@RequestScoped
public class AsyncRootResourceImpl implements AsyncRootResource {

   @Inject
   private AsyncSubResource subResource;

   @Override
   public AsyncSubResource getSubResource() {
      return subResource;
   }

   @GET
   public Response entered()
   {
      return Response.status(AsyncSubResourceImpl.methodEntered ? 444 : 200).build();
   }
}
