package org.jboss.resteasy.plugins.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.servlet.Servlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.protobuf.i18n.Messages;

import com.google.protobuf.Message;

@Provider
@Produces("application/grpc")
@Consumes("application/grpc")
@Priority(-1111)
public class GRPCProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T>
{
//   private static final Map<String, Servlet> servletMap = new HashMap<String, Servlet>();

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Message.class.isAssignableFrom(type);
   }

   @Override
   public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
               throws IOException, WebApplicationException
   {
      if (!(t instanceof Message))
      {
         throw new RuntimeException(Messages.MESSAGES.expectedMessage(type));
      }
      Message message = (Message) t;
      message.writeTo(entityStream);
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Message.class.isAssignableFrom(type);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
               throws IOException, WebApplicationException
   {
      if (!Message.class.isAssignableFrom(type))
      {
         throw new RuntimeException(Messages.MESSAGES.expectedMessage(type));
      }
      try
      {
            Method parseFrom = type.getDeclaredMethod("parseFrom", InputStream.class);
            parseFrom.setAccessible(true);
            return (T) parseFrom.invoke(null, entityStream);
      }
      catch (Exception e)
      {
         throw new WebApplicationException(e);
      }
   }

//   public static void addServlet(String name, Servlet servlet)
//   {
//      servletMap.put(name, servlet);
//   }
//
//   public static void removeServlet(String name)
//   {
//      servletMap.remove(name);
//   }
//
//   public static Servlet getServlet(String name)
//   {
//      return servletMap.get(name);
//   }
}
