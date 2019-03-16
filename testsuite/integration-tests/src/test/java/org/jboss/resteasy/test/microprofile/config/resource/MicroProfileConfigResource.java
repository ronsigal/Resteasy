package org.jboss.resteasy.test.microprofile.config.resource;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

@Path("/")
public class MicroProfileConfigResource {

   static {
     System.setProperty("system", "system-system");
   }

   @Inject Config config;

   @GET
   @Produces("text/plain")
   @Path("system/prog")
   public String systemProg() {
      return ConfigProvider.getConfig().getOptionalValue("system", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("system/inject")
   public String systemInject() {
      return config.getOptionalValue("system", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("init/prog")
   public String initProg() {
//      Config c = ConfigProvider.getConfig();
//      StringBuilder sb = new StringBuilder();
//      Iterable<ConfigSource> itb = c.getConfigSources();
//      for (ConfigSource cs : itb) {
//         System.out.println("ConfigSource: " + cs);
//         sb.append(cs).append("|");
//         Map<String, String> map = cs.getProperties();
//         for (Entry<String, String> e : map.entrySet()) {
//            System.out.println("  " + cs + ": " + e.getKey() + "->" + e.getValue());
//         }
//      }
////      Iterator<ConfigSource> it = c.getConfigSources().iterator();
////      for (ConfigSource cs = it.next(); it.hasNext(); ) {
////         System.out.println("ConfigSource: " + cs);
////         sb.append(cs).append("|");
////      }
//      return sb.toString();
      return ConfigProvider.getConfig().getOptionalValue("init", String.class).orElse("d'oh");
//      throw new RuntimeException("initProg()");
   }

   @GET
   @Produces("text/plain")
   @Path("init/inject")
   public String initInject() {
      return config.getOptionalValue("init", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("context/prog")
   public String contextProg() {
      return ConfigProvider.getConfig().getOptionalValue("context", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("context/inject")
   public String contextInject() {
      return config.getOptionalValue("context", String.class).orElse("d'oh");
   }
}