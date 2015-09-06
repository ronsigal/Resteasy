package org.jboss.resteasy.resteasy1186c;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

@RequestScoped
public class AsyncSubResourceImpl implements AsyncSubResource {

   @Override
   public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
      System.out.println("sub#getAll: beanParam#getParam valid? " + beanParam.getParam());
      asyncResponse.resume(Response.ok().build());
   }
   
}
