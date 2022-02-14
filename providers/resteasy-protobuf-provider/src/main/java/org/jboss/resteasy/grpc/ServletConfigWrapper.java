package org.jboss.resteasy.grpc;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletConfigWrapper implements ServletConfig {

   public static final String GRPC_JAXRS = "grpcJaxrs";
   ServletConfig delegate;

   public ServletConfigWrapper(ServletConfig delegate) {
      this.delegate = delegate;
   }

   @Override
   public String getServletName() {
      return delegate.getServletName();
   }

   @Override
   public ServletContext getServletContext() {
      return delegate.getServletContext();
   }

   @Override
   public String getInitParameter(String name) {
      if (GRPC_JAXRS.equals(name)) {
         return "true";
      }
      return delegate.getInitParameter(name);
   }

   @Override
   public Enumeration<String> getInitParameterNames() {
      return delegate.getInitParameterNames();
   }
}
