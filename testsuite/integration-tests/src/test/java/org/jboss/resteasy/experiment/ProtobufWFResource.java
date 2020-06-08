package org.jboss.resteasy.experiment;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class ProtobufWFResource
{
   @POST
   @Path("json")
   @Consumes("application/json")
   @Produces("application/json")
   public Person json(Person person) {
      person.setEmail("a@b");
      person.setId(3);
      person.setName("tanicka");
      return person;
   }

   @POST
   @Path("protobuf")
   @Consumes("application/protobuf")
   @Produces("application/protobuf")
   public Person proto(Person person) {
      person.setEmail("a@b");
      person.setId(3);
      person.setName("tanicka");
      return person;
   }
   
   @POST
   @Path("big/json")
   @Consumes("application/json")
   @Produces("application/json")
   public BigPerson bigJson(BigPerson person) throws Exception {
      BigPerson bp = ProtobufWFTest.getBigPerson(3, "tanicka", "a@b");
      return bp;
   }

   @POST
   @Path("big/protobuf")
   @Consumes("application/protobuf")
   @Produces("application/protobuf")
   public BigPerson bigProto(BigPerson person) throws Exception {
      BigPerson bp = ProtobufWFTest.getBigPerson(3, "tanicka", "a@b");
      return bp;
   }
}
