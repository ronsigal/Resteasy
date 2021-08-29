package io.grpc.classes;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequestHandler implements InvocationHandler {
   private String path;
   private String method;
   private InputStream is;
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private Map<String, String> headers = new HashMap<String, String>();
   

   public HttpServletRequestHandler(String path, String method, InputStream message, Map<String, String> headers) {
      this.path = path;
      this.method = method;
      this.is = message;
      this.headers = headers;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("getMethod".equals(method.getName())) {
         return method;
      }
      if ("getHeaderNames".equals(method.getName())) {
         return Collections.enumeration(new ArrayList<String>());
      }
      if ("getHeader".equals(method.getName())) {
         return headers.get(args[0]);
      }
      if ("getContentType".equals(method.getName())) {
         return "application/grpc";
      }
      if ("getRequestURL".equals(method.getName())) {
         return new StringBuffer("http://localhost:8081/" + path);
      }
      if ("getInputStream".equals(method.getName())) {
         return is;
      }
      if ("setAttribute".equals(method.getName())) {
         attributes.put((String) args[0], args[1]);
         return null;
      }
      if ("getAttribute".equals(method.getName())) {
         return attributes.get(args[0]);
      }
      return null;
   }
}
