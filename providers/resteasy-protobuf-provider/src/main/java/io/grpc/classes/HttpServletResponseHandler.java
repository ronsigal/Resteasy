package io.grpc.classes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HttpServletResponseHandler implements InvocationHandler {
   private MockServletOutputStream msos = new MockServletOutputStream();
   
   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("isCommitted".equals(method.getName())) {
         return true;
      }
      if ("getOutputStream".equals(method.getName())) {
         return msos;
      }
      return null;
   }
}
