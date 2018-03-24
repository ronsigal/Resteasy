package org.jboss.resteasy.rxjava;

import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import rx.Single;

public class SingleRxInvokerImpl implements SingleRxInvoker
{
   private final CompletionStageRxInvoker completionStageRxInvoker;
   private final SingleProvider singleProvider;

   public SingleRxInvokerImpl(CompletionStageRxInvoker completionStageRxInvoker, SingleProvider singleProvider)
   {
      this.completionStageRxInvoker = completionStageRxInvoker;
      this.singleProvider = singleProvider;
   }
   
   @Override
   public Single<?> get()
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.get());
   }

   @Override
   public <R> Single<?> get(Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.get(responseType));
   }

   @Override
   public <R> Single<?> get(GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.get(responseType));
   }

   @Override
   public Single<?> put(Entity<?> entity)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity));
   }

   @Override
   public <R> Single<?> put(Entity<?> entity, Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public <R> Single<?> put(Entity<?> entity, GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public Single<?> post(Entity<?> entity)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity));
   }

   @Override
   public <R> Single<?> post(Entity<?> entity, Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public <R> Single<?> post(Entity<?> entity, GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public Single<?> delete()
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.delete());
   }

   @Override
   public <R> Single<?> delete(Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.delete(responseType));
   }

   @Override
   public <R> Single<?> delete(GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.delete(responseType));
   }

   @Override
   public Single<?> head()
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.head());
   }

   @Override
   public Single<?> options()
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.options());
   }

   @Override
   public <R> Single<?> options(Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.options(responseType));
   }

   @Override
   public <R> Single<?> options(GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.options(responseType));
   }

   @Override
   public Single<?> trace()
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.trace());
   }

   @Override
   public <R> Single<?> trace(Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.trace(responseType));
   }

   @Override
   public <R> Single<?> trace(GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.trace(responseType));
   }

   @Override
   public Single<?> method(String name)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name));
   }

   @Override
   public <R> Single<?> method(String name, Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public <R> Single<?> method(String name, GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public Single<?> method(String name, Entity<?> entity)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity));
   }

   @Override
   public <R> Single<?> method(String name, Entity<?> entity, Class<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity, responseType));
   }

   @Override
   public <R> Single<?> method(String name, Entity<?> entity, GenericType<R> responseType)
   {
      return singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity, responseType));
   }
}
