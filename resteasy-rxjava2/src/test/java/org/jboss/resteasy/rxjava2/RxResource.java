package org.jboss.resteasy.rxjava2;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Stream;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("/")
public class RxResource
{
   
   private static boolean terminated = false;
   
   @Path("single")
   @GET
   public Single<String> single()
   {
      return Single.just("got it");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("observable")
   @GET
   public Observable<String> observable()
   {
      return Observable.fromArray("one", "two");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("flowable")
   @GET
   public Flowable<String> flowable()
   {
      return Flowable.fromArray("one", "two");
   }

   @Path("context/single")
   @GET
   public Single<String> contextSingle(@Context UriInfo uriInfo)
   {
      return Single.<String>create(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onSuccess("got it");
            }
         });
      }).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("context/observable")
   @GET
   public Observable<String> contextObservable(@Context UriInfo uriInfo)
   {
      return Observable.<String>create(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onNext("one");
               foo.onNext("two");
               foo.onComplete();
            }
         });
      }).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("context/flowable")
   @GET
   public Flowable<String> contextFlowable(@Context UriInfo uriInfo)
   {
      return Flowable.<String>create(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onNext("one");
               foo.onNext("two");
               foo.onComplete();
            }
         });
      }, BackpressureStrategy.BUFFER).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Stream
   @GET
   @Path("chunked")
   @Produces("application/json")
   public Publisher<String> chunked() {
      return Flowable.fromArray("one", "two");
   }

   @Stream
   @GET
   @Path("chunked-infinite")
   @Produces("application/json")
   public Publisher<String> chunkedInfinite() {
      terminated = false;
      System.err.println("Starting ");
      char[] chunk = new char[8192];
      Arrays.fill(chunk, 'a');
      String ret = new String(chunk);
      return Flowable.interval(1, TimeUnit.SECONDS).map(v -> {
         return ret;
      }).doFinally(() -> {
         terminated = true;
      });
   }
}
