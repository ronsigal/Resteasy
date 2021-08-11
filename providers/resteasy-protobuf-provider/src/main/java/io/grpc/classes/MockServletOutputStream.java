package io.grpc.classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class MockServletOutputStream extends ServletOutputStream {
   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
   
   @Override
   public boolean isReady()
   {
      return true;
   }

   @Override
   public void setWriteListener(WriteListener writeListener)
   {
   }

   @Override
   public void write(int b) throws IOException
   {
      baos.write(b);
   }
   
   public ByteArrayOutputStream getDelegate() {
      return baos;
   }
}