package org.jboss.resteasy.microprofile.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ServletConfigConfigSource implements ConfigSource {

   @Override
   public Map<String, String> getProperties() {
      ServletConfig config = ConfigContext.getServletConfig();
      System.out.println(this + ": config: " + config);
      if (config == null) {
         return null;
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = config.getInitParameterNames();
      for (String key = keys.nextElement(); keys.hasMoreElements(); key = keys.nextElement()) {
         map.put(key, config.getInitParameter(key));
         System.out.println(this + ": adding: " + key + "->" + config.getInitParameter(key));
      }
      return map;
   }

   @Override
   public String getValue(String propertyName) {
      ServletConfig config = ConfigContext.getServletConfig();
      if (config == null) {
         return null;
      }
      return config.getInitParameter(propertyName);
   }

   @Override
   public String getName() {
      ServletContext context = ConfigContext.getServletContext();
      ServletConfig config = ConfigContext.getServletConfig();
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
