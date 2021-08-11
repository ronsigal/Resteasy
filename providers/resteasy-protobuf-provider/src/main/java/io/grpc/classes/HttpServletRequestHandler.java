package io.grpc.classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Message;

public class HttpServletRequestHandler implements InvocationHandler {
   private String path;
   private Message message;
   private Map<String, Object> attributes = new HashMap<String, Object>();

   public HttpServletRequestHandler(String path, Message message) {
      this.path = path;
      this.message = message;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("getMethod".equals(method.getName())) {
         return "POST";
      }
      if ("getHeaderNames".equals(method.getName())) {
         return Collections.enumeration(new ArrayList<String>());
      }
      if ("getContentType".equals(method.getName())) {
         return "application/grpc";
      }
      if ("getRequestURL".equals(method.getName())) {
         return new StringBuffer("http://localhost:8081/" + path);
      }
      if ("getInputStream".equals(method.getName())) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         message.writeTo(baos);
         return new MockServletInputStream(new ByteArrayInputStream(baos.toByteArray()));
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
