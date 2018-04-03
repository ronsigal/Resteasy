package org.jboss.resteasy.spi;

import org.reactivestreams.Publisher;

public interface AsyncClientStreamProvider<T> {
   public T fromAsyncStream(Publisher<T> publisher);
}
