package org.jboss.resteasy.resteasy1186;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

@RequestScoped
public class AsyncSubResourceImpl implements AsyncSubResource {
   static boolean methodEntered;
   
   @Override
   public void getAll(@Suspended AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
      System.out.println("beanParam#getParam valid? " + beanParam.getParam());
      methodEntered = true;
      asyncResponse.resume(Response.ok().build());
   }
   
}
