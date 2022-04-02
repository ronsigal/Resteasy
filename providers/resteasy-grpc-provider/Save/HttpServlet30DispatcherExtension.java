package org.jboss.resteasy.plugins.grpc.servlet;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;

@SuppressWarnings("serial")
public class HttpServlet30DispatcherExtension extends HttpServlet30Dispatcher {//implements HttpRequestFactory, HttpResponseFactory {

   private ServletConfig servletConfig;

   public HttpServlet30DispatcherExtension(ServletContainerDispatcher servletContainerDispatcher) {
      this.servletContainerDispatcher = servletContainerDispatcher;
   }

   public void init(ServletConfig servletConfig) throws ServletException {
//      super.init(servletConfig);
      Map<Class<?>, Object> map = ResteasyContext.getContextDataMap();
      map.put(ServletContext.class, servletConfig.getServletContext());
      map.put(ServletConfig.class, servletConfig);
      this.servletConfig = servletConfig;
   }

   /**
    * Returns this servlet's {@link ServletConfig} object.
    *
    * @return ServletConfig the <code>ServletConfig</code> object that initialized this servlet
    */
   @Override
   public ServletConfig getServletConfig() {
       return servletConfig;
   }
}
