package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class GZIPAnnotationInterceptor implements ReaderInterceptor {
   
   @Override
   public Object aroundReadFrom(ReaderInterceptorContext ctx) throws IOException {
      
      if (ctx.getHeaders().get(HttpHeaders.CONTENT_ENCODING).get(0).contains("gzip")) {
         return ctx.proceed();
      } else {
         throw new RuntimeException("no gzip in Content-Encoding");
      }
   }
}
