package org.jboss.resteasy.rxjava;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientStreamProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import rx.Observable;
import rx.RxReactiveStreams;
import rx.plugins.RxJavaHooks;

@Provider
public class ObservableProvider<T> implements AsyncStreamProvider<Observable<?>>, AsyncClientStreamProvider<Observable<?>>
{

   static
   {
      RxJavaHooks.setOnObservableCreate(new ResteasyContextPropagatingOnObservableCreateAction());
   }

   @Override
   public Publisher<?> toAsyncStream(Observable<?> asyncResponse)
   {
      return RxReactiveStreams.toPublisher(asyncResponse);
   }

   @Override
   public Observable<?> fromAsyncStream(Publisher<Observable<?>> publisher)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
