package io.grpc.classes;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {
   private InputStream is;
   
   public MockServletInputStream(InputStream is) {
      this.is = is;
   }
   
   @Override
   public boolean isFinished() {
      try {
         return is.available() > 0;
      } catch (IOException e) {
         return true;
      }
   }

   @Override
   public boolean isReady() {
      return true;
   }

   @Override
   public void setReadListener(ReadListener readListener) { 
   }

   @Override
   public int read() throws IOException {
      return is.read();
   }
}