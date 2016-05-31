package org.jboss.resteasy.specimpl;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.ws.rs.core.NioInputStream;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 28, 2016
 */
public class ResteasyNioInputStream extends NioInputStream
{
   private ServletInputStream inputStream;
   
   public ResteasyNioInputStream(ServletInputStream inputStream)
   {
      this.inputStream = inputStream;
   }
   
   @Override
   public boolean isFinished()
   {
      return inputStream.isFinished();
   }

   @Override
   public int read() throws IOException
   {
      return inputStream.read();
   }
}
