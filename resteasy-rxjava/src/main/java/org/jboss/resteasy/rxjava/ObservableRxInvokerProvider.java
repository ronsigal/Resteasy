package org.jboss.resteasy.rxjava;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.Provider;

import rx.Observable;
import rx.Observer;
import rx.observables.AsyncOnSubscribe;

@Provider
public class ObservableRxInvokerProvider implements RxInvokerProvider<ObservableRxInvoker>
{
   WebTarget target;
   
   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return ObservableRxInvoker.class.equals(clazz) || Observable.class.equals(clazz);
   }

   @Override
   public ObservableRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      return new ObservableRxInvokerImpl(syncInvoker, executorService);
   }

   public WebTarget getTarget()
   {
      return target;
   }

   public void setTarget(WebTarget target)
   {
      this.target = target;
   }
   
   private class ObservableAsyncOnSubscribe<T, S> extends AsyncOnSubscribe<S, T>
   {

      @Override
      protected S generateState()
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      protected S next(S state, long requested, Observer<Observable<? extends T>> observer)
      {
         // TODO Auto-generated method stub
         return null;
      }


   }
}
