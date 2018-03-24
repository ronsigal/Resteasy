package org.jboss.resteasy.rxjava2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;

@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>, AsyncClientResponseProvider<Single<?>>
{
   static
   {
      RxJavaPlugins.setOnSingleSubscribe(new ResteasyContextPropagatingOnSingleCreateAction());
   }

   private static class SingleAdaptor<T> extends CompletableFuture<T>
   {
      private Disposable subscription;

      public SingleAdaptor(Single<T> observable)
      {
         this.subscription = observable.subscribe(this::complete, this::completeExceptionally);
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         subscription.dispose();
         return super.cancel(mayInterruptIfRunning);
      }
   }
   
   private static class CompletionToSingle<T> extends Single<T>
   {
      private CompletionStage<T> completionStage;
      
      public CompletionToSingle(CompletionStage<T> completionStage)
      {
         this.completionStage = completionStage;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      protected void subscribeActual(SingleObserver<? super T> observer)
      {
         Future<? super T> future = completionStage.toCompletableFuture();
         try 
         {
            Object o = future.get();
            observer.onSuccess((T) o);
         }
         catch (Exception e)
         {
            observer.onError(e);
         }
      }
   }

   @Override
   public CompletionStage<?> toCompletionStage(Single<?> asyncResponse)
   {
      return new SingleAdaptor<>(asyncResponse);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Single<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return new CompletionToSingle(completionStage);
   }

}
