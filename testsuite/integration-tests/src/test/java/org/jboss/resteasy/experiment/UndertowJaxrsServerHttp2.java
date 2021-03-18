package org.jboss.resteasy.experiment;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;

public class UndertowJaxrsServerHttp2 extends UndertowJaxrsServer
{
   @Override
   public UndertowJaxrsServerHttp2 start()
   {
      server = Undertow.builder()
         .addHttpListener(8081, "localhost")
         .setHandler(root)
         .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
         .build();
      server.start();
      return this;
   }
}
