package org.jboss.resteasy.specimpl;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.ws.rs.core.NioErrorHandler;
import javax.ws.rs.core.NioWriterHandler;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 28, 2016
 */
public class WriteListenerWrapper implements WriteListener
{
   private NioWriterHandler handler;
   private ResteasyNioOutputStream outputStream;
   private NioErrorHandler errorHandler;
   
   public WriteListenerWrapper(NioWriterHandler handler, NioErrorHandler errorHandler, ServletOutputStream outputStream) throws IOException
   {
      this.handler = handler;
      this.errorHandler = errorHandler;
      this.outputStream = new ResteasyNioOutputStream(outputStream);
   }
   
   @Override
   public void onWritePossible() throws IOException
   {
      handler.write(outputStream);
   }

   @Override
   public void onError(Throwable t)
   {
      try
      {
         errorHandler.error(t);
      }
      catch (Throwable e)
      {
         LogMessages.LOGGER.error("bummer");
      }
   }
}
