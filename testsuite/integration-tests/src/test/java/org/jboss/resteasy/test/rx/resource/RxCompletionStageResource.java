package org.jboss.resteasy.test.rx.resource;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public interface RxCompletionStageResource {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> get() throws InterruptedException;

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> getThing() throws InterruptedException;

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> getThingList() throws InterruptedException;

   @PUT
   @Path("put/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> put(String s) throws InterruptedException;

   @PUT
   @Path("put/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> putThing(String s) throws InterruptedException;

   @PUT
   @Path("put/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> putThingList(String s) throws InterruptedException;

   @POST
   @Path("post/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> post(String s) throws InterruptedException;

   @POST
   @Path("post/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> postThing(String s) throws InterruptedException;

   @POST
   @Path("post/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> postThingList(String s) throws InterruptedException;

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> delete() throws InterruptedException;

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> deleteThing() throws InterruptedException;

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> deleteThingList() throws InterruptedException;

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> head() throws InterruptedException;

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> options() throws InterruptedException;

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> optionsThing() throws InterruptedException;

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> optionsThingList() throws InterruptedException;

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   public CompletionStage<String> trace() throws InterruptedException;

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<Thing> traceThing() throws InterruptedException;

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public CompletionStage<List<Thing>> traceThingList() throws InterruptedException;

   @GET
   @Path("exception/unhandled")
   public CompletionStage<Thing> exceptionUnhandled() throws Exception;

   @GET
   @Path("exception/handled")
   public CompletionStage<Thing> exceptionHandled() throws Exception;
}
