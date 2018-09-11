package org.jboss.resteasy.plugins.providers;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.CookieParam;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;
import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
@Provider
@Priority(Integer.MAX_VALUE)
public class MultiValuedParamConverter implements ParamConverter<Collection<?>> 
{
   private StringParameterInjector stringParameterInjector;
   private Annotation[] annotations;
   private Constructor<?> constructor;
   private Pattern pattern;
   private String separator;

   public MultiValuedParamConverter(Class<?> rawType, Annotation[] annotations, StringParameterInjector stringParameterInjector)
   {
      this.stringParameterInjector = stringParameterInjector;
      this.annotations = annotations;
      this.constructor = getConstructor(rawType);
      if (constructor == null)
      {
         throw new RuntimeException("ooops");
      }
      pattern = getPattern(annotations);
      if (pattern == null)
      {
         separator = getSeparator(annotations);
      }
   }

   @Override
   public String toString(Collection<?> value)
   {
      new Exception("entering toString(): value: " + value).printStackTrace();;
      if (value == null || value.isEmpty())
      {
         System.out.println("toString() returning");
         return null;
      }
      String s = stringify(value);
      System.out.println("stringify(): " + s);
      return s;
//      return stringify(value);
   }

   @Override
   public Collection<?> fromString(String param)
   {
      try
      {
         Collection<?> c = (Collection<?>) constructor.newInstance();
         String[] params = pattern != null ? splitByRegex(param) : param.split(separator);
         return parse(c, params);
      }
      catch (InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private Constructor<?> getConstructor(Class<?> clazz)
   {
      try
      {
         if (List.class.equals(clazz) || ArrayList.class.equals(clazz))
         {
            return ArrayList.class.getConstructor();
         }
         else if (SortedSet.class.equals(clazz) || TreeSet.class.equals(clazz))
         {
            return TreeSet.class.getConstructor();
         }
         else if (Set.class.equals(clazz) || HashSet.class.equals(clazz))
         {
            return HashSet.class.getConstructor();
         }
         else if (clazz.isArray())
         {
            return clazz.getConstructor();
         }
      }
      catch (NoSuchMethodException e)
      {
         return null;
      }
      return null;
   }

   private String getSeparator(Annotation[] annotations)
   {
      for (Annotation a : annotations)
      {
         if (a instanceof Separator)
         {
            return ((Separator) a).value();
         }
      }
      
      for (Annotation a : annotations)
      {
         if (a instanceof CookieParam)
         {
            return "-";
         }
      }
      return ",";
   }

   private Pattern getPattern(Annotation[] annotations)
   {
      for (Annotation a : annotations)
      {
         if (a instanceof Regex)
         {
            return Pattern.compile(((Regex) a).value());
         }
      }
      return null;
   }

   private String[] splitByRegex(String s)
   {
      Matcher matcher = pattern.matcher(s);
      ArrayList<String> list = new ArrayList<String>();
      while (matcher.find()) {
         for (int i = 1; i <= matcher.groupCount(); i++) {
            list.add(matcher.group(i));
         }
      }
      return list.toArray(new String[list.size()]);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private Collection<?> parse(Collection c, String[] params)
   {
      try
      {
         for (String param : params)
         {
            c.add(stringParameterInjector.extractValue(param));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      return c;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private String stringify(Collection<?> value)
   {
      System.out.println("entering stringify()");
      Object o = value.iterator().next();
      ParamConverter converter = ResteasyProviderFactory.getInstance().getParamConverter(o.getClass(), o.getClass().getGenericSuperclass(), annotations);
      System.out.println("converter: " + converter);
      Method saveAsString = null;
      if (converter == null)
      {
         try
         {
            saveAsString = o.getClass().getMethod("saveAsString");
         }
         catch (Exception e)
         {
            // ok
         }  
      }
      System.out.println("saveAsString: " + saveAsString);
      StringBuffer sb = new StringBuffer();
      boolean first = true;
      try
      {
         for (Object s : value)
         {
            if (first)
            {
               first = false;
            }
            else
            {
               sb.append(separator);
            }
            if (s instanceof String)
            {
               System.out.println("s: " + s);
               sb.append(s);
            }
            else if (converter != null)
            {
               System.out.println("s: converter.toString()" + converter.toString(s));
               sb.append(converter.toString(s)); 
            }
            else if (saveAsString != null)
            { System.out.println("s: saveAsString.invoke(s)" + saveAsString.invoke(s));
               sb.append(saveAsString.invoke(s));
            }
            else {
               System.out.println("s?");
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      System.out.println("sb: " + sb.toString());
      return sb.toString();
   }
}
