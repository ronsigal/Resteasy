package org.jboss.resteasy.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

@SuppressWarnings("unchecked")
public class ConfigContext
{
   private static final ThreadLocal<Map<Class<?>, Object>> contextualData = new ThreadLocal<Map<Class<?>, Object>>();

   public static <T> void pushContext(Class<T> type, T data)
   {
      getMap().put(type, data);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getMap().get(type);
   }

   public static ServletConfig getServletConfig()
   {
      return getContextData(ServletConfig.class);
   }

   public static void putServletConfig(ServletConfig config)
   {
      getMap().put(ServletConfig.class, config);
   }

   public static ServletContext getServletContext()
   {
      return getContextData(ServletContext.class);
   }

   public static void putServletContext(ServletContext context)
   {
      getMap().put(ServletContext.class, context);
   }

   public static void clear()
   {
      contextualData.set(null);
   }

   private static Map<Class<?>, Object> getMap()
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null)
      {
         map = new HashMap<Class<?>, Object>();
         contextualData.set(map);
      }
      return map;
   }
}
