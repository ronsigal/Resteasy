package org.jboss.resteasy.specimpl;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.core.NioOutputStream;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 28, 2016
 */
public class ResteasyNioOutputStream extends NioOutputStream
{
   private ServletOutputStream outputStream;
   
   public ResteasyNioOutputStream(ServletOutputStream outputStream)
   {
      this.outputStream = outputStream;  
   }
   
   @Override
   public void write(int b) throws IOException
   {
      outputStream.write(b);
   }
}
