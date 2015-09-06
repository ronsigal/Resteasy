package org.jboss.resteasy.resteasy1186c;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;


@RequestScoped
public class AsyncRootResourceImpl extends AbstractAsyncRootResource {

   @Inject
   private AsyncSubResource subResource;
   
   @Override
   public AsyncSubResource getSubResource() {
      return subResource;
   }

}
