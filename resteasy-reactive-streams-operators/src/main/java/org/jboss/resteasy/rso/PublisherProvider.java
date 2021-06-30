package org.jboss.resteasy.rso;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

@Provider
public class PublisherProvider implements AsyncStreamProvider<Publisher<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Publisher<?> asyncResponse)
   {
      return asyncResponse;
   }
}
