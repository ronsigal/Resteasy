package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.reactivex.Single;

public interface SingleProxy {
   
   @GET
   @Path("textSingle")
   @Produces("text/plain")
   public Single<String> textSingle();
}
