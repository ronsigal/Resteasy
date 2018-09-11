package org.jboss.resteasy.plugins.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
@Provider
public class MultiValuedParamConverterProvider implements ParamConverterProvider
{
   private StringParameterInjector stringParameterInjector;
   
    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
       if (Collection.class.isAssignableFrom(rawType)) 
       {   
          // test parameter type for conversion
          Class<?> type = Types.getTypeArgument(genericType);
          if (type == null)
          {
             return null;
          }
          stringParameterInjector = new StringParameterInjector(type, null, null, null, null, null, annotations, ResteasyProviderFactory.getInstance());
          return (ParamConverter<T>) new MultiValuedParamConverter(rawType, annotations, stringParameterInjector);
       }
//       else if (rawType.isArray())
//       {
//          Class<?> type = rawType.getComponentType();
//          stringParameterInjector = new StringParameterInjector(type, null, null, null, null, null, annotations, ResteasyProviderFactory.getInstance());
//          return (ParamConverter<T>) new MultiValuedParamConverter(rawType, annotations, stringParameterInjector);
//       }
       return null;
    }
}
