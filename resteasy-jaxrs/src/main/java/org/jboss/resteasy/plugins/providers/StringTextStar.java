package org.jboss.resteasy.plugins.providers;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Chunked;
import org.jboss.resteasy.util.NoContent;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class StringTextStar implements MessageBodyReader<String>, MessageBodyWriter<String>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return String.class.equals(type);
   }

   public String readFrom(Class<String> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException
   {
      if (NoContent.isContentLengthZero(httpHeaders)) return "";
      return ProviderHelper.readString(entityStream, mediaType);
   }


   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return String.class.equals(type);
   }

   public long getSize(String o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(String o,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {  
      String charset = mediaType.getParameters().get("charset");
      byte[] bytes = charset == null ? o.getBytes(StandardCharsets.UTF_8) : o.getBytes(charset);
      Chunked chunked = ResteasyProviderFactory.getContextData(Chunked.class);
      if (chunked != null &&
            !httpHeaders.containsKey(HttpHeaders.TRANSFER_ENCODING) && 
            !httpHeaders.containsKey(HttpHeaders.CONTENT_LENGTH))
      {
         if (chunked.isChunked())
         {
            List<Object> headers = new ArrayList<Object>();
            headers.add("chunked");
            httpHeaders.put(HttpHeaders.TRANSFER_ENCODING, headers);
         }
         else
         {
            List<Object> headers = new ArrayList<Object>();
            headers.add(bytes.length);
            httpHeaders.put(HttpHeaders.CONTENT_LENGTH, headers);
         }
      }
      entityStream.write(bytes);
//      if (charset == null) entityStream.write(o.getBytes(StandardCharsets.UTF_8));
//      else entityStream.write(o.getBytes(charset));

   }
}