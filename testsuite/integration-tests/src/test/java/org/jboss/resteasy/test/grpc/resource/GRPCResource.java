package org.jboss.resteasy.test.grpc.resource;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public class GRPCResource
{
   static {
      try
      {
         HelloWorldServer_gRPC.main(null);
      }
      catch (IOException | InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   
   @Path("HelloWorldProto.Greeter")
   public static class TestResource {

      @POST
      @Path("sayHello")
      @Consumes("application/grpc")
      @Produces("application/grpc")
      public HelloReply sayHello(HelloRequest request) throws Exception {
         return HelloReply.newBuilder().setMessage("well hello " + request.getName()).build();
      }
   }
}
