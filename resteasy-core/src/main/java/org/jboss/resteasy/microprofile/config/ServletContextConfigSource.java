package org.jboss.resteasy.microprofile.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.core.ResteasyContext;

public class ServletContextConfigSource implements ConfigSource {

   @Override
   public Map<String, String> getProperties() {
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return Collections.<String, String>emptyMap();
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = context.getInitParameterNames();
      for (String key = keys.nextElement(); keys.hasMoreElements(); key = keys.nextElement()) {
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
      ServletContext servletContext = ResteasyContext.getContextData(ServletContext.class);
      FilterConfig filterConfig = ResteasyContext.getContextData(FilterConfig.class);
      ServletConfig servletConfig = ResteasyContext.getContextData(ServletConfig.class);
      return (servletContext != null ? servletContext.getServletContextName() : null) + 
            ":" + (filterConfig != null ? filterConfig.getFilterName() : null) +
            ":" + (servletConfig != null ? servletConfig.getServletName() : null) + 
            ":ServletContextConfigSource";
   }

   @Override
   public int getOrdinal() {
      return 40;
   }
}
