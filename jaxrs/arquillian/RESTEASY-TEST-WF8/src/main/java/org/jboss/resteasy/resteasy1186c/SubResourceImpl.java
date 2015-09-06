package org.jboss.resteasy.resteasy1186c;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;

@RequestScoped
public class SubResourceImpl implements SubResource {
   static boolean methodEntered;
   
   @Override
   public Response getAll(QueryBeanParamImpl beanParam) {
      System.out.println("beanParam#getParam valid? " + beanParam.getParam());
      methodEntered = true;
      return Response.ok().build();
   }
   
}
