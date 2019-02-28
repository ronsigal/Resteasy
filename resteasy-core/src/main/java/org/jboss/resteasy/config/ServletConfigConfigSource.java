package org.jboss.resteasy.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.core.ResteasyContext;

public class ServletConfigConfigSource implements ConfigSource {

   @Override
   public Map<String, String> getProperties() {
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (config == null) {
         return null;
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = config.getInitParameterNames();
      for (String key = keys.nextElement(); keys.hasMoreElements();) {
         map.put(key, config.getInitParameter(key));
      }
      return map;
   }

   @Override
   public String getValue(String propertyName) {
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (config == null) {
         return null;
      }
      return config.getInitParameter(propertyName);
   }

   @Override
   public String getName() {
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (context == null || config == null) {
         return null;
      }
      return context.getServletContextName() + ":" + config.getServletName() + ":ServletConfigConfigSource";
   }

   @Override
   public int getOrdinal() {
      return 50;
   }
}
