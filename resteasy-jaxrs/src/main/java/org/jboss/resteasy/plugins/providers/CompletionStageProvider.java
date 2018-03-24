package org.jboss.resteasy.plugins.providers;

import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;

public class CompletionStageProvider implements AsyncResponseProvider<CompletionStage<?>>, AsyncClientResponseProvider<CompletionStage<?>> {

   @SuppressWarnings("rawtypes")
   @Override
   public CompletionStage toCompletionStage(CompletionStage<?> asyncResponse)
   {
      return asyncResponse;
   }

   @Override
   public CompletionStage<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return completionStage;
   }
}
