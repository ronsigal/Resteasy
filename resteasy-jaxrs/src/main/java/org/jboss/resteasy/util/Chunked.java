package org.jboss.resteasy.util;

public class Chunked
{
   private boolean chunked;
   
   public Chunked(boolean chunked)
   {
      this.chunked = chunked;
   }
   
   public boolean isChunked()
   {
      return chunked;
   }
}
