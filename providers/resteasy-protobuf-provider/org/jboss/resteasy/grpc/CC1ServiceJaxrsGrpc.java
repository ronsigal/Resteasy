package io.grpc.classes;

import io.grpc.stub.StreamObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.resteasy.core.ResteasyContext;
import io_grpc_classes___CC2;
import io_grpc_classes___CC4;
import Integer;

public final class CC1ServiceJaxrsGrpc extends CC1ServiceGrpc {

   @java.lang.Override
   public void m1(io_grpc_classes___CC2 param, StreamObserver<io.grpc.classes.CC1_proto.String> responseObserver) {
      try {
         HttpServletResponse response = getHttpServletResponse();
         Servlet servlet = ResteasyContext.getServlet("ResteasyServlet");
         servlet.service(getHttpServletRequest(param), response);
         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();
         ByteArrayOutputStream baos = msos.getDelegate();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         io.grpc.classes.CC1_proto.String s = String.parseFrom(bais);
         responseObserver.onNext(s);
      } catch (Exception e) {
         responseObserver.onError(e);
         return;
      }
      responseObserver.onCompleted();
   }

   @java.lang.Override
   public void m3(io_grpc_classes___CC4 param, StreamObserver<String> responseObserver) {
      try {
         HttpServletResponse response = getHttpServletResponse();
         Servlet servlet = ResteasyContext.getServlet("ResteasyServlet");
         servlet.service(getHttpServletRequest(param), response);
         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();
         ByteArrayOutputStream baos = msos.getDelegate();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         HelloReply helloReply = HelloReply.parseFrom(bais);
         responseObserver.onNext(helloReply);
      } catch (Exception e) {
         responseObserver.onError(e);
         return;
      }
      responseObserver.onCompleted();
   }

   @java.lang.Override
   public void m4(Integer param, StreamObserver<Boolean> responseObserver) {
      try {
         HttpServletResponse response = getHttpServletResponse();
         Servlet servlet = ResteasyContext.getServlet("ResteasyServlet");
         servlet.service(getHttpServletRequest(param), response);
         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();
         ByteArrayOutputStream baos = msos.getDelegate();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         HelloReply helloReply = HelloReply.parseFrom(bais);
         responseObserver.onNext(helloReply);
      } catch (Exception e) {
         responseObserver.onError(e);
         return;
      }
      responseObserver.onCompleted();
   }

   static HttpServletRequest getHttpServletRequest(Message message) {
      return (HttpServletRequest) Proxy.newProxyInstance(
         HttpServletRequest.class.getClassLoader(),
         new Class[] { HttpServletRequest.class },
         new HttpServletRequestHandler("HelloWorldProto.Greeter/sayHello", message))
   }

   static HttpServletResponse getHttpServletResponse() {
      return (HttpServletResponse) Proxy.newProxyInstance(
         HttpServletResponse.class.getClassLoader(),
         new Class[] { HttpServletResponse.class },
         new HttpServletResponseHandler())
   }

}

