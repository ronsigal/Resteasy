package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("misc")
public class MultiValuedParamDefaultParamConverterMiscResource {
 
   @Path("whitespace")
   @GET
   public String whiteSpace(@QueryParam("w") @Regex("([^- ]+)") List<String> list) {
      StringBuffer sb = new StringBuffer();
      for (String s : list) {
         sb.append(s).append("|");
      }
      return sb.toString();
   }
   
   @Path("precedence")
   @GET
   public String precedence(@QueryParam("p") @Separator("-") @Regex("([^;]+)") List<String> list) {
      StringBuffer sb = new StringBuffer();
      for (String s : list) {
         sb.append(s).append("|");
      }
      return sb.toString();
   }
}