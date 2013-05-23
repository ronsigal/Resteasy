package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistryListener;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.jboss.resteasy.core.registry.Segment;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;
import org.jboss.resteasy.spi.validation.ValidationSupport;
import org.jboss.resteasy.util.FeatureContextDelegate;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.WeightedMediaType;
import org.jboss.resteasy.validation.ViolationsContainer;

import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodInvoker implements ResourceInvoker, JaxrsInterceptorRegistryListener
{
   protected List<WeightedMediaType> preferredProduces = new ArrayList<WeightedMediaType>();
   protected List<WeightedMediaType> preferredConsumes = new ArrayList<WeightedMediaType>();
   protected MethodInjector methodInjector;
   protected InjectorFactory injector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory parentProviderFactory;
   protected ResteasyProviderFactory resourceMethodProviderFactory;
   protected ResourceMethod method;
   protected ContainerRequestFilter[] requestFilters;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected ConcurrentHashMap<String, AtomicLong> stats = new ConcurrentHashMap<String, AtomicLong>();
   protected ViolationsContainer violationsContainer;
   protected ResourceInfo resourceInfo;

   protected boolean methodConsumes;


   public ResourceMethodInvoker(ResourceMethod method, InjectorFactory injector, ResourceFactory resource, ResteasyProviderFactory providerFactory)
   {
      this.injector = injector;
      this.resource = resource;
      this.parentProviderFactory = providerFactory;
      this.method = method;

       resourceInfo = new ResourceInfo()
      {
         @Override
         public Method getResourceMethod()
         {
            return ResourceMethodInvoker.this.method.getAnnotatedMethod();
         }

         @Override
         public Class<?> getResourceClass()
         {
            return ResourceMethodInvoker.this.method.getResourceClass().getClazz();
         }
      };

      this.resourceMethodProviderFactory = new ResteasyProviderFactory(providerFactory);
      for (DynamicFeature feature : providerFactory.getServerDynamicFeatures())
      {
         feature.configure(resourceInfo, new FeatureContextDelegate(resourceMethodProviderFactory));
      }

      this.methodInjector = injector.createMethodInjector(method, resourceMethodProviderFactory);

      for (MediaType mediaType : method.getProduces())
      {
         preferredProduces.add(WeightedMediaType.parse(mediaType));

      }

      // hack for when message contentType == null
      // todo this needs review on why it is here and what do we need it for
      methodConsumes = method.getAnnotatedMethod().isAnnotationPresent(Consumes.class);

      for (MediaType mediaType : method.getConsumes())
      {
         preferredConsumes.add(WeightedMediaType.parse(mediaType));

      }

      Collections.sort(preferredProduces);
      Collections.sort(preferredConsumes);

      requestFilters = resourceMethodProviderFactory.getContainerRequestFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      responseFilters = resourceMethodProviderFactory.getContainerResponseFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      writerInterceptors = resourceMethodProviderFactory.getServerWriterInterceptorRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());


      // we register with parent to lisen for redeploy evens
      providerFactory.getContainerRequestFilterRegistry().getListeners().add(this);
      providerFactory.getContainerResponseFilterRegistry().getListeners().add(this);
      providerFactory.getServerWriterInterceptorRegistry().getListeners().add(this);
   }

   public void cleanup()
   {
      parentProviderFactory.getContainerRequestFilterRegistry().getListeners().remove(this);
      parentProviderFactory.getContainerResponseFilterRegistry().getListeners().remove(this);
      parentProviderFactory.getServerWriterInterceptorRegistry().getListeners().remove(this);
      for (ValueInjector param : methodInjector.getParams())
      {
         if (param instanceof MessageBodyParameterInjector)
         {
            parentProviderFactory.getServerReaderInterceptorRegistry().getListeners().remove(param);
         }
      }
   }

   public void registryUpdated(JaxrsInterceptorRegistry registry)
   {
      this.resourceMethodProviderFactory = new ResteasyProviderFactory(parentProviderFactory);
      for (DynamicFeature feature : parentProviderFactory.getServerDynamicFeatures())
      {
         feature.configure(resourceInfo, new FeatureContextDelegate(resourceMethodProviderFactory));
      }
      if (registry.getIntf().equals(WriterInterceptor.class))
      {
         writerInterceptors = resourceMethodProviderFactory.getServerWriterInterceptorRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      }
      else if (registry.getIntf().equals(ContainerRequestFilter.class))
      {
         requestFilters = resourceMethodProviderFactory.getContainerRequestFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      }
      else if (registry.getIntf().equals(ContainerResponseFilter.class))
      {
         responseFilters = resourceMethodProviderFactory.getContainerResponseFilterRegistry().postMatch(method.getResourceClass().getClazz(), method.getAnnotatedMethod());
      }
   }


   protected void incrementMethodCount(String httpMethod)
   {
      AtomicLong stat = stats.get(httpMethod);
      if (stat == null)
      {
         stat = new AtomicLong();
         AtomicLong old = stats.putIfAbsent(httpMethod, stat);
         if (old != null) stat = old;
      }
      stat.incrementAndGet();
   }

   /**
    * Key is httpMethod called
    *
    * @return
    */
   public Map<String, AtomicLong> getStats()
   {
      return stats;
   }

   public ContainerRequestFilter[] getRequestFilters()
   {
      return requestFilters;
   }

   public ContainerResponseFilter[] getResponseFilters()
   {
      return responseFilters;
   }

   public WriterInterceptor[] getWriterInterceptors()
   {
      return writerInterceptors;
   }

   public Type getGenericReturnType()
   {
      return method.getGenericReturnType();
   }

   public Class<?> getResourceClass()
   {
      return method.getResourceClass().getClazz();
   }

   public Annotation[] getMethodAnnotations()
   {
      return method.getAnnotatedMethod().getAnnotations();
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredProduces()
   {
      return preferredProduces;
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredConsumes()
   {
      return preferredConsumes;
   }

   public Method getMethod()
   {
      return method.getMethod();
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response)
   {
      Object target = resource.createResource(request, response, resourceMethodProviderFactory);
      return invoke(request, response, target);
   }

   public BuiltResponse invoke(HttpRequest request, HttpResponse response, Object target)
   {
      request.setAttribute(ResourceMethodInvoker.class.getName(), this);
      incrementMethodCount(request.getHttpMethod());
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      uriInfo.pushCurrentResource(target);
      try
      {
         BuiltResponse jaxrsResponse = invokeOnTarget(request, response, target);

         if (jaxrsResponse != null && jaxrsResponse.getEntity() != null)
         {
            // if the content type isn't set, then set it to be either most desired type from the Accept header
            // or the first media type in the @Produces annotation
            // See RESTEASY-144
            Object type = jaxrsResponse.getMetadata().getFirst(
                    HttpHeaderNames.CONTENT_TYPE);
            if (type == null)
               jaxrsResponse.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, resolveContentType(request, jaxrsResponse.getEntity()));
         }
         return jaxrsResponse;

      }
      finally
      {
         uriInfo.popCurrentResource();
      }
   }

   protected BuiltResponse invokeOnTarget(HttpRequest request, HttpResponse response, Object target)
   {
      Validator validator = ValidationSupport.getValidator();
      if (validator != null)
      {
         try
         {
            violationsContainer = new ViolationsContainer(validator.validate(target));
         }
         catch (Exception e)
         {
            violationsContainer = new ViolationsContainer();
            violationsContainer.setException(e);
            throw new ResteasyViolationException(violationsContainer);
         }
         request.setAttribute(ViolationsContainer.class.getName(), violationsContainer);
      }

      PostMatchContainerRequestContext requestContext = new PostMatchContainerRequestContext(request, this);
      for (ContainerRequestFilter filter : requestFilters)
      {
         try
         {
            filter.filter(requestContext);
         }
         catch (IOException e)
         {
            throw new ApplicationException(e);
         }
         BuiltResponse serverResponse = (BuiltResponse)requestContext.getResponseAbortedWith();
         if (serverResponse != null)
         {
            return serverResponse;
         }
      }

      Object rtn = methodInjector.invoke(request, response, target);

      if (violationsContainer != null)
      {
         if (violationsContainer.getException() != null || violationsContainer.size() > 0)
         {
            {
               throw new ResteasyViolationException(violationsContainer);
            }
         }
      }
         
      if (request.getAsyncContext().isSuspended())
      {
         request.getAsyncContext().getAsyncResponse().setAnnotations(method.getAnnotatedMethod().getAnnotations());
         request.getAsyncContext().getAsyncResponse().setWriterInterceptors(writerInterceptors);
         request.getAsyncContext().getAsyncResponse().setResponseFilters(responseFilters);
         request.getAsyncContext().getAsyncResponse().setMethod(this);
         return null;
      }
      if (rtn == null || method.getReturnType().equals(void.class))
      {
         BuiltResponse build = (BuiltResponse) Response.noContent().build();
         build.setAnnotations(method.getAnnotatedMethod().getAnnotations());
         return build;
      }
      if (Response.class.isAssignableFrom(method.getReturnType()) || rtn instanceof Response)
      {
         BuiltResponse rtn1 = (BuiltResponse) rtn;
         rtn1.setAnnotations(method.getAnnotatedMethod().getAnnotations());
         return rtn1;
      }

      Response.ResponseBuilder builder = Response.ok(rtn);
      builder.type(resolveContentType(request, rtn));
      BuiltResponse jaxrsResponse = (BuiltResponse)builder.build();
      jaxrsResponse.setGenericType(method.getGenericReturnType());
      jaxrsResponse.setAnnotations(method.getAnnotatedMethod().getAnnotations());
      return jaxrsResponse;
   }

   public boolean doesProduce(List<? extends MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         //System.out.println("**** no accepts " +" method: " + method);
         return true;
      }
      if (preferredProduces.size() == 0)
      {
         //System.out.println("**** no produces " +" method: " + method);
         return true;
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : preferredProduces)
         {
            if (type.isCompatible(accept))
            {
               return true;
            }
         }
      }
      return false;
   }

   public boolean doesConsume(MediaType contentType)
   {
      boolean matches = false;
      if (contentType == null)
      {
         // If there is no @Consumes annotation directly on method (i.e. a @GET or @DELETE) return true
         // this is a hack to determine if this is a @GET or @DELETE.  Because we can create new HTTP methods ad hoc
         // there is no way to determine if it is an HTTP method that doesn't require content (like PUT, POST)
         // So, we just check to see if there is an annotation directly on the method.
         if (!methodConsumes) return true;

         // Otherwise only accept if consumes is a wildcard type
         for (MediaType type : preferredConsumes)
         {
            if (type.equals(MediaType.WILDCARD_TYPE))
            {
               return true;
            }
         }
         return false;
      }
      else
      {
         if (preferredConsumes.size() == 0) return true;
         else
         {
            for (MediaType type : preferredConsumes)
            {
               if (type.isCompatible(contentType))
               {
                  matches = true;
                  break;
               }
            }
         }
      }
      return matches;
   }

   public MediaType resolveContentType(HttpRequest in, Object entity)
   {
      MediaType chosen = (MediaType)in.getAttribute(Segment.RESTEASY_CHOSEN_ACCEPT);
      if (chosen != null  && !chosen.equals(MediaType.WILDCARD_TYPE))
      {
         return chosen;
      }

      List<MediaType> accepts = in.getHttpHeaders().getAcceptableMediaTypes();

      if (accepts == null || accepts.size() == 0)
      {
         if (preferredProduces.size() == 0) return MediaType.WILDCARD_TYPE;
         else return preferredProduces.get(0);
      }

      if (preferredProduces.size() == 0)
      {
         return resolveContentTypeByAccept(accepts, entity);
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : preferredProduces)
         {
            if (type.isCompatible(accept)) return type;
         }
      }
      return MediaType.WILDCARD_TYPE;
   }

   protected MediaType resolveContentTypeByAccept(List<MediaType> accepts, Object entity)
   {
      if (accepts == null || accepts.size() == 0 || entity == null)
      {
         return MediaType.WILDCARD_TYPE;
      }
      Class clazz = entity.getClass();
      Type type = this.method.getGenericReturnType();
      if (entity instanceof GenericEntity)
      {
         GenericEntity gen = (GenericEntity) entity;
         clazz = gen.getRawType();
         type = gen.getType();
      }
      for (MediaType accept : accepts)
      {
         if (resourceMethodProviderFactory.getMessageBodyWriter(clazz, type, method.getAnnotatedMethod().getAnnotations(), accept) != null)
         {
            return accept;
         }
      }
      return MediaType.WILDCARD_TYPE;
   }

   public Set<String> getHttpMethods()
   {
      return method.getHttpMethods();
   }

   public MediaType[] getProduces()
   {
      return method.getProduces();
   }

   public MediaType[] getConsumes()
   {
      return method.getConsumes();
   }
}
