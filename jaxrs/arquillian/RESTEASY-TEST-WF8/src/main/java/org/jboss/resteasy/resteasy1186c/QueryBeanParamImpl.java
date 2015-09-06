package org.jboss.resteasy.resteasy1186c;

import javax.ws.rs.QueryParam;


public class QueryBeanParamImpl implements QueryBeanParam {

   @QueryParam("foo")
    private String param;
   
   @Override
   public String getParam() {
      return param;
   }
}
