package org.jboss.resteasy.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.core.ResteasyContext;

public class ServletContextConfigSource implements ConfigSource {

   @Override
   public Map<String, String> getProperties() {
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return null;
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = context.getInitParameterNames();
      for (String key = keys.nextElement(); keys.hasMoreElements(); ) {
         map.put(key, context.getInitParameter(key));
      }
      return map;
   }

   @Override
   public String getValue(String propertyName) {
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return null;
      }
      return context.getInitParameter(propertyName);
   }

   @Override
   public String getName() {
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return null;
      }
      return context.getServletContextName() + ":ServletConfigContextSource";
   }

   @Override
   public int getOrdinal() {
      return 40;
   }
}
