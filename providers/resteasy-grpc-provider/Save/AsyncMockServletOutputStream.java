package io.grpc.classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.Cleanable;
import org.jboss.resteasy.plugins.server.Cleanables;

public class AsyncMockServletOutputStream extends MockServletOutputStream implements Cleanable {
   
//   private CountDownLatch latch = new CountDownLatch(1);
//   private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();
   private LinkedBlockingQueue<ByteArrayOutputStream> queue = new LinkedBlockingQueue<ByteArrayOutputStream>();
   private static final ByteArrayOutputStream EMPTY = new ByteArrayOutputStream();
   private boolean addedToCleanables;
   
   public AsyncMockServletOutputStream() {

   }
   
   public ByteArrayOutputStream await() throws InterruptedException {
      System.out.println("entering AsyncMockServletOutputStream.await()");
      while (true) {
         try {
//            latch.await();
            ByteArrayOutputStream baos = queue.take();
            System.out.println("leaving AsyncMockServletOutputStream.await(): " + baos.toString());
            return baos;
//            return;
         } catch (InterruptedException e) {
            //
         }
      }
   }

   public void release(ByteArrayOutputStream baos) throws IOException {
      if (!addedToCleanables) {
         Cleanables cleanables = ResteasyContext.getContextData(Cleanables.class);
         if (cleanables != null) {
            cleanables.addCleanable(this);
            addedToCleanables = true;
            System.out.println("added " + this + " to Cleanables");
         }
      }
      //      latch.countDown();
      System.out.println("entering AsyncMockServletOutputStream.release(): " + baos.toString());
      while (true) {
         try {
            queue.put(baos);
            break;
         } catch (InterruptedException e) {
            //
         }
      }
      System.out.println("leaving AsyncMockServletOutputStream.release()");
   }

   public void renew() {
      baos = new ByteArrayOutputStream();
   }
   
   @Override
   public void close() throws IOException {
      new Exception("AsyncMockServletOutputStream.close()").printStackTrace();
      super.close();
      while (true) {
         try {
            queue.put(EMPTY);
            break;
         }
         catch (InterruptedException e) {
            //
         }
      }
   }

   @Override
   public void clean() throws Exception {
      System.out.println("cleaning " + this);
      close();
   }
}
