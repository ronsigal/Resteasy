package org.jboss.resteasy.experiment;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("")
public class Test2795Resource {

   @Path("test")
   @GET
   @Test2795Constraint(message="testMessage", value="testValue")
   public String m1() {
      System.out.println("entering m1()");
      return "test";
   }

   @GET
   @Path("throw")
   @Test2795Constraint(message="throwMessage", value="throwValue")
   public String m2() {
      System.out.println("entering m2()");
      Set<? extends ConstraintViolation<?>> set = new HashSet<ConstraintViolation<String>>();
      throw new Test2795Exception("throw", set);
   }
}
