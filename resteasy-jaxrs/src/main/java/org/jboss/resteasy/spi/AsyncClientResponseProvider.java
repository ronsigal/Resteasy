package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

/**
 * Used to turn a CompletionStage into another reactive class.
 * Can be used for implementing RxInvokers for other suitable classes.
 *
 * @param <T>
 */
public interface AsyncClientResponseProvider<T> {

   public T fromCompletionStage(CompletionStage<?> completionStage);
}