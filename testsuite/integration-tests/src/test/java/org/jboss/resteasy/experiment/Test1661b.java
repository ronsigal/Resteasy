package org.jboss.resteasy.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.spi.Graph;
import org.eclipse.microprofile.reactive.streams.operators.spi.ReactiveStreamsEngine;
import org.eclipse.microprofile.reactive.streams.operators.spi.SubscriberWithCompletionStage;
import org.eclipse.microprofile.reactive.streams.operators.spi.UnsupportedStageException;
import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.rso.PublisherRxInvoker;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class Test1661b {

   private static UndertowJaxrsServer server;
   private static Client client;
   
   private static CountDownLatch latch = new CountDownLatch(1);
   private static AtomicInteger errors = new AtomicInteger(0);
   private static ArrayList<String> stringList = new ArrayList<String>();
   
   @Path("/")
   public static class TestResource {

      @Path("pb")
      @GET
      @Stream
      @Produces("text/plain")
      public PublisherBuilder<String> publisherBuilder()
      {
         return ReactiveStreams.of("x", "y");
      }
      
      @Path("p")
      @GET
      @Stream
      @Produces("text/plain")
      public Publisher<String> publisher()
      {
         return ReactiveStreams.of("x", "y").buildRs();
      }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
      client = (ResteasyClient) ResteasyClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testPublisherBuilderGet() throws Exception {
      
      PublisherRxInvoker invoker = client.target("http://localhost:8081/pb").request().rx(PublisherRxInvoker.class);
      Publisher<String> publisher = (Publisher<String>) invoker.get();
      publisher.subscribe(new Subscriber<String>() {

         @Override
         public void onSubscribe(Subscription s) {
            //
         }
         @Override
         public void onNext(String t) {
            stringList.add(t);         }
         @Override
         public void onError(Throwable t) {
            errors.incrementAndGet();
         }
         @Override
         public void onComplete() {
            latch.countDown();
         }
         
      });
      boolean waitResult = latch.await(5, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      for (String s : stringList) {
         System.out.println(" " + s);
      }
   }
   
   class TestReactiveStreamsEngine implements ReactiveStreamsEngine
   {
      private ReactiveStreamsEngine delegate;
      
      TestReactiveStreamsEngine(ReactiveStreamsEngine engine)
      {
         this.delegate = engine;
      }
      @Override
      public <T> Publisher<T> buildPublisher(Graph graph) throws UnsupportedStageException
      {
         return delegate.buildPublisher(graph);
      }
      @Override
      public <T, R> SubscriberWithCompletionStage<T, R> buildSubscriber(Graph graph) throws UnsupportedStageException
      {
         return delegate.buildSubscriber(graph);
      }
      @Override
      public <T, R> Processor<T, R> buildProcessor(Graph graph) throws UnsupportedStageException
      {
         return delegate.buildProcessor(graph);
      }
      @Override
      public <T> CompletionStage<T> buildCompletion(Graph graph) throws UnsupportedStageException
      {
         return delegate.buildCompletion(graph);
      }
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testPublisherGet() throws Exception {
      ReactiveStreamsEngine defaultEngine = new io.smallrye.reactive.streams.Engine();
      ReactiveStreamsEngine engine = new TestReactiveStreamsEngine(defaultEngine);
      PublisherRxInvoker invoker = client.target("http://localhost:8081/p").request().rx(PublisherRxInvoker.class).reactiveStreamsEngine(engine);
      Publisher<String> publisher = (Publisher<String>) invoker.get();
      publisher.subscribe(new Subscriber<String>() {

         @Override
         public void onSubscribe(Subscription s) {
            //
         }
         @Override
         public void onNext(String t) {
            stringList.add(t);         }
         @Override
         public void onError(Throwable t) {
            errors.incrementAndGet();
         }
         @Override
         public void onComplete() {
            latch.countDown();
         }  
      });
      boolean waitResult = latch.await(5, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      for (String s : stringList) {
         System.out.println(" " + s);
      }
   }
}
