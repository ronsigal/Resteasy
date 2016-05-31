package org.jboss.resteasy.specimpl;

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.ws.rs.core.NioCompletionHandler;
import javax.ws.rs.core.NioErrorHandler;
import javax.ws.rs.core.NioReaderHandler;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 28, 2016
 */
public class ReadListenerWrapper implements ReadListener
{
   private NioReaderHandler handler;
   private ResteasyNioInputStream inputStream;
   private NioCompletionHandler completionHandler;
   private NioErrorHandler errorHandler;
   
   public ReadListenerWrapper(NioReaderHandler handler, ServletInputStream inputStream, NioCompletionHandler completionHandler, NioErrorHandler errorHandler)
   {   
      this.handler = handler;
      this.inputStream = new ResteasyNioInputStream(inputStream);
      this.completionHandler = completionHandler;
      this.errorHandler = errorHandler;
   }
   
   @Override
   public void onDataAvailable() throws IOException
   {
      handler.read(inputStream);
   }
   
   @Override
   public void onAllDataRead() throws IOException
   {
      completionHandler.complete(inputStream);
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
