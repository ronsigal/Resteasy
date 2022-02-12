package io.grpc.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AsyncContextExp implements AsyncContext {
   
   private ServletRequest servletRequest;
   private ServletResponse servletResponse;
   private Set<AsyncListener> listeners = new HashSet<AsyncListener>();
   private Executor executor;
   long timeout;

   public AsyncContextExp(ServletRequest servletRequest, ServletResponse servletResponse, Executor executor) {
      this.servletRequest = servletRequest;
      this.servletResponse = servletResponse;
      this.executor = executor;
   }
   @Override
   public ServletRequest getRequest() {
      return servletRequest;
   }

   @Override
   public ServletResponse getResponse() {
      return servletResponse;
   }

   @Override
   public boolean hasOriginalRequestAndResponse()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void dispatch()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void dispatch(String path)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void dispatch(ServletContext context, String path)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void complete()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void start(Runnable run) {
//      executor.execute(new Runnable() {
//          @Override
//          public void run() {
//              servletRequestContext.getCurrentServletContext().invokeRunnable(exchange, run);
//          }
//      });
   }

   @Override
   public void addListener(AsyncListener listener) {
      listeners.add(listener);
   }

   @Override
   public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setTimeout(long timeout) {
      this.timeout = timeout;
}

   @Override
   public long getTimeout() {
      return timeout;
   }

}
