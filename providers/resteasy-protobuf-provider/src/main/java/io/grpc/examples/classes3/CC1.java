package io.grpc.examples.classes3;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class CC1 {
   
   @Path("m1")
   @GET
   String m1(CC2 cc2) {
      return "x";
   }

   String m2(String s) {
      return "x";
   }
}

