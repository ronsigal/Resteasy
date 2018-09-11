package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractCollectionProcessor<T>
{
   protected String paramName;
   protected Type type;
   protected Annotation[] annotations;

   public AbstractCollectionProcessor(String paramName)
   {
      this.paramName = paramName;
   }
   
   public AbstractCollectionProcessor(String paramName, Type type, Annotation[] annotations)
   {
      this.paramName = paramName;
      this.type = type;
      this.annotations = annotations;
   }

   protected abstract T apply(T target, Object object);

   public T buildIt(T target, Object object)
   {
      if (object == null) return target;
      if (object instanceof Collection)
      {
         if (annotations != null && type != null)
         {
            object = ResteasyProviderFactory.getInstance().toString(object, object.getClass(), type, annotations);
            target = apply(target, object);
         }
         else
         {
            for (Object obj : (Collection<?>) object)
            {
               target = apply(target, obj);
            }
         }
      }
      else if (object.getClass().isArray())
      {
         if (object.getClass().getComponentType().isPrimitive())
         {
            Class<?> componentType = object.getClass().getComponentType();
            if (componentType.equals(boolean.class))
            {
               for (boolean bool : (boolean[]) object) target = apply(target, bool);
            }
            else if (componentType.equals(byte.class))
            {
               for (byte val : (byte[]) object) target = apply(target, val);
            }
            else if (componentType.equals(short.class))
            {
               for (short val : (short[]) object) target = apply(target, val);
            }
            else if (componentType.equals(int.class))
            {
               for (int val : (int[]) object) target = apply(target, val);
            }
            else if (componentType.equals(long.class))
            {
               for (long val : (long[]) object) target = apply(target, val);
            }
            else if (componentType.equals(float.class))
            {
               for (float val : (float[]) object) target = apply(target, val);
            }
            else if (componentType.equals(double.class))
            {
               for (double val : (double[]) object) target = apply(target, val);
            }
         }
         else
         {
            Object[] objs = (Object[]) object;
            for (Object obj : objs)
            {
               target = apply(target, obj);

            }
         }
      }
      else
      {
         target = apply(target, object);
      }
      return target;
   }
}
