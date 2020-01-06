package org.jboss.resteasy.plugins.providers.jsonb;

import org.eclipse.yasson.JsonBindingProvider;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.glassfish.json.JsonProviderImpl;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.json.bind.Jsonb;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.nio.charset.Charset;


/**
 * Created by rsearls
 */
public class AbstractJsonBindingProvider extends JsonBindingProvider {

   private static final String JSON = "json";
   private static final String PLUS_JSON = "+json";

   @Context
   javax.ws.rs.ext.Providers providers;
   private static Jsonb jsonbObj = null;

   protected static volatile Boolean isJaxbAcceptible = null;

   protected Jsonb getJsonb(Class<?> type) {
      ContextResolver<Jsonb> contextResolver = providers.getContextResolver(Jsonb.class, MediaType.APPLICATION_JSON_TYPE);
      if (contextResolver != null)
      {
         return contextResolver.getContext(type);
      } else
      {
         if (jsonbObj == null)
         {
            JsonProviderImpl jProviderImpl = new JsonProviderImpl();
            JsonBindingBuilder jbBuilder = new JsonBindingBuilder();
            jsonbObj = jbBuilder.withProvider(jProviderImpl).build();
         }
         return jsonbObj;
      }
   }
   
   protected boolean isJaxbAcceptible() {
      if (isJaxbAcceptible != null) {
         return isJaxbAcceptible;
      }
      synchronized (AbstractJsonBindingProvider.class) {
         if (isJaxbAcceptible != null) {
            return isJaxbAcceptible;
         }
         ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
         if (context != null)
         {
            String s = context.getParameter(ResteasyContextParameters.RESTEASY_JAXB_OVER_JSONB);
            if (s != null) {
               isJaxbAcceptible = Boolean.parseBoolean(s);
            }
         }
         else {
            isJaxbAcceptible = Boolean.FALSE;
         }
      }
      return isJaxbAcceptible;
   }

   public static Charset getCharset(final MediaType mediaType) {
      return Charset.forName("utf-8");
   }

   public static boolean isSupportedMediaType(final MediaType mediaType) {
      return mediaType.getSubtype().equals(JSON) || mediaType.getSubtype().endsWith(PLUS_JSON);
   }
}
