package org.jboss.resteasy.plugins.protobuf.sse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.sse.OutboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;
import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

@Provider
@Produces({"text/event-stream", "application/x-stream-general"})
@Consumes({"text/event-stream", "application/x-stream-general"})
public class SseReaderWriter extends SseEventProvider
{   
   private MessageBodyWriter<Object> mbw;
   private Providers providers;

   public SseReaderWriter() {
   }

   protected SseReaderWriter(MessageBodyWriter<Object> mbw, Providers providers) {
      this.mbw = mbw;
      this.providers = providers;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return OutboundSseEvent.class.isAssignableFrom(type) &&
            (MediaType.SERVER_SENT_EVENTS_TYPE.isCompatible(mediaType) ||
            GENERAL_STREAM_TYPE.isCompatible(mediaType));
   }

   @Override
   public long getSize(OutboundSseEvent t, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(OutboundSseEvent event, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      SseEvent sseEvent = convertToSseEvent(event, type, genericType, annotations, mediaType, httpHeaders);
      mbw.writeTo(sseEvent, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }

//   @Override
//   @SuppressWarnings({})
//   public CompletionStage<Void> asyncWriteTo(OutboundSseEvent event, Class<?> type, Type genericType,
//                                             Annotation[] annotations, MediaType mediaType,
//                                             MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream)
//   {
//      try {
//         SseEvent sseEvent = convertToSseEvent(event, type, genericType, annotations, mediaType, httpHeaders);
//         CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
//         ret.thenApply(() -> mbw.writeTo(sseEvent, type, genericType, annotations, mediaType, httpHeaders, entityStream));
//         mbw.writeTo(sseEvent, type, genericType, annotations, mediaType, httpHeaders, entityStream);
//      }
//      catch (WebApplicationException | IOException e) {
//         throw new RuntimeException(e);
//      }
//   }

   @SuppressWarnings("unchecked")
   private SseEvent convertToSseEvent(OutboundSseEvent event, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders) throws WebApplicationException, IOException {
//      Charset charset = StandardCharsets.UTF_8;
      SseEvent sseEvent = new SseEvent();
      if (event.getComment() != null) {
         sseEvent.setComment(event.getComment());
      }
      sseEvent.setName(event.getName());
      if (event.getId() != null) {
         sseEvent.setId(Integer.valueOf(event.getId()));
      }
      if (event.getData() != null) {
         
         Class<?> payloadClass = event.getType();
         Type payloadType = event.getGenericType();
         if (payloadType == null) {
            payloadType = payloadClass;
         }
         if (payloadType == null && payloadClass == null) {
            payloadType = Object.class;
            payloadClass = Object.class;
         }
         @SuppressWarnings("rawtypes")
         MessageBodyWriter writer = providers.getMessageBodyWriter(payloadClass,
               payloadType, annotations, event.getMediaType());
         if (writer == null) {
            throw new ServerErrorException(Messages.MESSAGES.notFoundMBW(payloadClass.getName()),
                  Response.Status.INTERNAL_SERVER_ERROR);
         }
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         writer.writeTo(event.getData(), payloadClass, payloadType, annotations, mediaType, httpHeaders, baos);
         sseEvent.setData(baos.toByteArray());
      }
      sseEvent.setRetry(event.getReconnectDelay());
      return sseEvent;
   }
}
