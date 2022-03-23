package org.jboss.resteasy.plugins.grpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jboss.logging.Logger;

public class JaxrsImplBaseExtender {

   private static Logger logger = Logger.getLogger(JaxrsImplBaseExtender.class);
   private static String contextPath = "";

   private String packageName = "";
   private String outerClassName = "";
   private String serviceName = "";
   private String servletName = "";
   private Set<String> imports = new HashSet<String>();
   
   public static void main(String[] args) {
      if (args.length != 3) {
         logger.info("need three args:");
         logger.info("  arg[0]: .proto file prefix");
         logger.info("  arg[1]: servlet name");
         logger.info("  arg[2]: context path");
         return;
      }
      contextPath = args[2];
      new JaxrsImplBaseExtender(args);
   }

   public JaxrsImplBaseExtender(String[] args) {
      servletName = args[1];
      parse(args[0]);
   }

   private void parse(String root) {
      File file = new File("./src/main/proto/" + root + ".proto");
      if (!file.exists()) {
         throw new RuntimeException(root + ".proto not found");
      }
      try {
         StringBuilder sbHeader = new StringBuilder();
         StringBuilder sbBody = new StringBuilder();
         Reader reader = new FileReader(file);
         Scanner scanner = new Scanner(reader);
         classHeader(scanner, sbHeader, root);
         String s = scanner.findWithinHorizon("service ", 0);
         while (s != null) {
            serviceName = scanner.next();
            sbHeader.append("import ")
                    .append(packageName).append(".")
                    .append(serviceName).append("Grpc").append(".")
                    .append(serviceName).append("ImplBase;\n");
            service(scanner, sbHeader, sbBody, root);
            s = scanner.findWithinHorizon("service ", 0);
         }
         sbHeader.append("\n");
         staticMethods(sbBody);
         sbBody.append("}\n");
         writeClass(sbHeader, sbBody);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void classHeader(Scanner scanner, StringBuilder sb, String fileName) {
      String pkg = null;
      String s = scanner.findWithinHorizon("java_package", 0);
      if (s != null) {
         scanner.findWithinHorizon("\"", 0);
         scanner.useDelimiter("[ \"]");
         pkg = scanner.next();
      } else {
         s = scanner.findWithinHorizon("package", 0);
         if (s != null) {
            scanner.useDelimiter("[ ;]");
            pkg = scanner.next();
         }
      }
      sb.append("package ").append(pkg).append(";\n\n");
      packageName = pkg;
      s = scanner.findWithinHorizon("java_outer_classname", 0);
      if (s != null) {
         s = scanner.findWithinHorizon("=", 0);
         s = scanner.findWithinHorizon("\"", 0);
         outerClassName = scanner.next();
      }
      imports(scanner, sb, fileName);
      scanner.reset();
   }

   private void imports(Scanner scanner, StringBuilder sb, String fileName) {
      sb.append("import com.google.protobuf.ByteString;\n")
        .append("import com.google.protobuf.GeneratedMessageV3;\n")
        .append("import com.google.protobuf.Message;\n")
        .append("import io.grpc.stub.StreamObserver;\n")
        .append("import java.io.ByteArrayInputStream;\n")
        .append("import java.io.ByteArrayOutputStream;\n")
        .append("import java.io.IOException;\n")
        .append("import java.io.InputStream;\n")
        .append("import java.lang.reflect.Proxy;\n")
        .append("import java.util.ArrayList;\n")
        .append("import java.util.concurrent.ExecutorService;\n")
        .append("import java.util.HashMap;\n")
        .append("import java.util.Iterator;\n")
        .append("import java.util.List;\n")
        .append("import java.util.Map;\n")
        .append("import javax.ws.rs.core.MediaType;\n")
        .append("import javax.servlet.Servlet;\n")
        .append("import javax.servlet.ServletContext;\n")
        .append("import javax.servlet.http.HttpServletRequest;\n")
        .append("import javax.servlet.http.HttpServletResponse;\n")
        .append("import org.jboss.resteasy.core.ResteasyContext;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.ServletConfigWrapper;\n")
        .append("import org.jboss.resteasy.plugins.grpc.sse.SseEvent;\n")
        .append("import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;\n")
        .append("import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;\n")
        .append("import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;\n")
        .append("import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.AsyncContextImpl;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.AsyncMockServletOutputStream;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.HttpServletRequestImpl;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.HttpServletRequestHandler;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.HttpServletResponseHandler;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.MockServletInputStream;\n")
        .append("import org.jboss.resteasy.plugins.grpc.servlet.MockServletOutputStream;\n")
        .append("import javax.inject.Inject;\n")
        .append("import javax.enterprise.inject.spi.CDI;\n")
        .append("import javax.enterprise.context.RequestScoped;\n")
        .append("import javax.enterprise.context.ContextNotActiveException;\n")
        .append("import org.jboss.weld.module.web.context.http.HttpRequestContextImpl;\n")
        .append("import org.jboss.weld.manager.BeanManagerImpl;\n")
        .append("import org.jboss.weld.bean.builtin.BeanManagerProxy;\n")
        .append("import javax.enterprise.inject.spi.BeanManager;\n")
        .append("import com.google.protobuf.Any;\n")
        .append("import test.grpc.").append(fileName).append("_Server;\n");
   }

   private void service(Scanner scanner, StringBuilder sbHeader, StringBuilder sbBody, String root) {
      sbBody.append("public class ")
            .append(serviceName)
            .append("GrpcImpl extends ")
            .append(serviceName)
            .append("ImplBase {\n\n")
            .append("   BeanManager manager = CDI.current().getBeanManager();\n\n");
      scanner.nextLine();
      scanner.skip("//");
      String path = scanner.next();
      String actualEntityClass = scanner.next();
      String httpMethod = scanner.next();
      String syncType = scanner.next();
      String rpc = scanner.findWithinHorizon(" rpc ", 0);
      while (rpc != null) {
         rpc(scanner, root, actualEntityClass, httpMethod, syncType, sbHeader, sbBody);
         scanner.nextLine();
         if (!scanner.hasNext("//")) {
            break;
         }
         scanner.skip("//");
         path = scanner.next();
         actualEntityClass = scanner.next();
         httpMethod = scanner.next();
         syncType = scanner.next();
         rpc = scanner.findWithinHorizon(" rpc ", 0);
      }
   }

   private void rpc(Scanner scanner, String root, String actualEntityClass, String httpMethod, String syncType, StringBuilder sbHeader, StringBuilder sbBody) {
      sbBody.append("\n   @java.lang.Override\n");
      String method = scanner.next();
      scanner.findWithinHorizon("\\(", 0);
      scanner.useDelimiter("\\)");
      String param = getParamType(packageName, outerClassName, scanner.next());
      if (!imports.contains(actualEntityClass)) {
         sbHeader.append("import " + packageName + "." + outerClassName + "." + actualEntityClass + ";\n");
         imports.add(actualEntityClass);
      }
      scanner.findWithinHorizon("returns", 0);
      scanner.findWithinHorizon("\\(", 0);
      String retn = getReturnType(packageName, outerClassName, scanner.next());
      if (!imports.contains(retn)) {
         sbHeader.append("import " + retn + ";\n");
         imports.add(retn);
      }
      sbBody.append("   public void ")
            .append(method).append("(")
            .append(param).append(" param, ")
            .append("StreamObserver<").append(retn).append("> responseObserver) {\n");
      rpcBody(scanner, root, actualEntityClass, httpMethod, syncType, sbBody, retn);
      sbBody.append("   }\n");
      scanner.reset();
   }

   private void rpcBody(Scanner scanner, String root, String actualEntityClass, String method, String syncType, StringBuilder sb, String retn) {
      sb.append("      try {\n")
        .append("         HttpServletResponse response = getHttpServletResponse(\"" + retn + "\", \"" + syncType + "\");\n")
        .append("         HttpServletDispatcher servlet = (HttpServletDispatcher) ResteasyContext.getServlet(\"").append(servletName).append("\");\n") // plug in correct servlet
        .append("         HttpServlet30Dispatcher hs30d = new HttpServlet30Dispatcher();\n")
        .append("         String readerWriter = getClass().getPackage()").append(".toString().substring(\"package \".length()) + \".\" + \"").append(root).append("\" + \"MessageBodyReaderWriter\";\n")
        .append("         hs30d.init(new ServletConfigWrapper(servlet.getServletConfig(), readerWriter));\n")
        .append("         ").append(actualEntityClass).append(" actualParam = param.").append(getGetterMethod(actualEntityClass)).append(";\n")
        .append("         String url = param.getURL();\n")
        .append("         ByteArrayInputStream bais = new ByteArrayInputStream(actualParam.toByteArray());\n")
        .append("         MockServletInputStream msis = new MockServletInputStream(bais);\n")
        .append("         Map<String, List<String>> headers = convertHeaders(param.getHeadersMap());\n")
        .append("         javax.servlet.http.Cookie[] cookies = convertCookies(param.getCookiesList());\n")
        .append("         ServletContext servletContext = CC1_Server.getContext();\n")
        .append("         HttpServletRequest request = new HttpServletRequestImpl(response, servletContext, \"").append(contextPath).append("\", url, \"").append(method).append("\", msis, \"").append(retn).append("\", headers, cookies);\n")
        .append("         HttpRequestContextImpl context = new HttpRequestContextImpl(\"/jaxrs.example.grpc-0.0.1-SNAPSHOT.war\");\n")
        .append("         context.associate(request);\n")
        .append("         context.activate();\n")
        .append("         BeanManager bm = CDI.current().getBeanManager();\n")
        .append("         try {\n")
        .append("            bm.getContext(RequestScoped.class);\n")
        .append("         } catch (ContextNotActiveException e) {\n")
        .append("            BeanManagerProxy bmp = (BeanManagerProxy) bm;\n")
        .append("            BeanManagerImpl bmi = bmp.delegate();\n")
        .append("            bmi.addContext(context);\n")
        .append("         }\n")
        .append("         hs30d.service(\"").append(method).append("\", request, response);\n");

      if ("suspended".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
           .append("         amsos.await();\n")
           .append("         ByteArrayOutputStream baos = amsos.getDelegate();\n")
           .append("         ByteArrayInputStream bais1 = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         com.google.protobuf.Any reply = com.google.protobuf.Any.parseFrom(bais1);\n")
           .append("         responseObserver.onNext(reply);\n");
      } else if ("completionStage".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
           .append("         amsos.await();\n")
           .append("         ByteArrayOutputStream baos = amsos.getDelegate();\n")
           .append("         ByteArrayInputStream bais1 = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         ").append(retn).append(" reply = ").append(retn).append(".parseFrom(bais1);\n")
           .append("         responseObserver.onNext(reply);\n");
      }
      
      else if ("sse".equals(syncType)) {
//         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
//           .append("         ByteArrayOutputStream baos = msos.getDelegate();\n")
//           .append("         bais = new ByteArrayInputStream(baos.toByteArray());\n")
//           .append("         bais.mark(0);\n")
//           .append("         while (bais.read() != -1) {\n")
//           .append("            bais.reset();\n")
//           .append("            responseObserver.onNext(transformSseEvent(bais));\n")
//           .append("            bais.mark(0);\n")
//           .append("         }\n")
           ;
         // temporary
//         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
//         .append("         ByteArrayOutputStream baos = msos.getDelegate();\n");
//         
//         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
//           .append("         while (true) {\n")
//           .append("            ByteArrayOutputStream baos = amsos.await();\n")
//           .append("            if (amsos.isClosed()) {\n")
//           .append("               break;\n")
//           .append("            }\n")
//           .append("            byte[] bytes = baos.toByteArray();\n")
//           .append("            if (bytes.length == 2 && bytes[0] == 10 && bytes[1] == 10) {\n")
//           .append("               continue;\n")
//           .append("            }\n")
//           .append("            try {\n")
//           .append("System.out.println(\"sending \" + baos.toString());\n")
//           .append("               org_jboss_resteasy_plugins_protobuf_sse___SseEvent reply = org_jboss_resteasy_plugins_protobuf_sse___SseEvent.parseFrom(bytes);\n")
//           .append("               responseObserver.onNext(reply);\n")
//           .append("            } catch (Exception e) {\n")
//           .append("               continue;\n")
//           .append("            }\n")           
//           .append("         }\n");
      } else {
         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
           .append("         ByteArrayOutputStream baos = msos.getDelegate();\n")
           .append("         bais = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         ").append(retn).append(" reply = ").append(retn).append(".parseFrom(bais);\n")
           .append("         responseObserver.onNext(reply);\n");
      }
      sb.append("      } catch (Exception e) {\n")
        .append("         e.printStackTrace();\n")
        .append("         responseObserver.onError(e);\n")
        .append("      } finally {\n")
        .append("         responseObserver.onCompleted();\n")
        .append("      }\n");
   }

   private static void staticMethods(StringBuilder sb) {
      sb.append("\n")
        .append("   private static HttpServletRequest getHttpServletRequest(String path, String method, InputStream is, String className, String retn) {\n")
        .append("      Map<String, String> headers = new HashMap<String, String>();\n")
        .append("      headers.put(\"javabuf-name\", className);\n")
        .append("      if (\"com.google.protobuf.Any\".equals(retn)) {\n")
        .append("         headers.put(HttpServletResponseHandler.GRPC_RETURN_RESPONSE, \"true\");\n")
        .append("      }\n")
        .append("      return (HttpServletRequest) Proxy.newProxyInstance(\n")
        .append("         HttpServletRequest.class.getClassLoader(),\n")
        .append("         new Class[] { HttpServletRequest.class },\n")
        .append("         new HttpServletRequestHandler(\"").append(contextPath).append("\", path, method, is, headers));\n")
        .append("   }\n\n");
      sb.append("   private static HttpServletResponse getHttpServletResponse(String retn, String syncType) {\n")
        .append("      return (HttpServletResponse) Proxy.newProxyInstance(\n")
        .append("         HttpServletResponse.class.getClassLoader(),\n")
        .append("         new Class[] { HttpServletResponse.class },\n")
        .append("         new HttpServletResponseHandler(retn, syncType));\n")
        .append("   }\n\n");
      sb.append("   private static Map<String, List<String>> convertHeaders(Map<String, jaxrs.example.CC1_proto.Header> protoHeaders) {\n")
        .append("      Map<String, List<String>> headers = new HashMap<String, List<String>>();\n")
        .append("      for (Map.Entry<String, jaxrs.example.CC1_proto.Header> entry : protoHeaders.entrySet()) {\n")
        .append("         String key = entry.getKey();\n")
        .append("         jaxrs.example.CC1_proto.Header protoHeader = entry.getValue();\n")
        .append("         List<String> values = new ArrayList<String>();\n")
        .append("         for (int i = 0; i < protoHeader.getValuesCount(); i++) {\n")
        .append("            values.add(protoHeader.getValues(i));\n")
        .append("         }\n")
        .append("         headers.put(key, values);\n")
        .append("      }\n")
        .append("      return headers;\n")
        .append("   }\n\n");
      sb.append("   private static javax.servlet.http.Cookie[] convertCookies(List<jaxrs.example.CC1_proto.Cookie> cookieList) {\n")
        .append("      javax.servlet.http.Cookie[] cookieArray = new javax.servlet.http.Cookie[cookieList.size()];\n")
        .append("      int i = 0;\n")
        .append("      for (Iterator<jaxrs.example.CC1_proto.Cookie> it = cookieList.iterator(); it.hasNext(); ) {\n")
        .append("         jaxrs.example.CC1_proto.Cookie protoCookie = it.next();\n")
        .append("         javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(protoCookie.getName(), protoCookie.getValue());\n")
        .append("         cookie.setVersion(protoCookie.getVersion());\n")
        .append("         cookie.setPath(protoCookie.getPath());\n")
        .append("         cookie.setDomain(protoCookie.getDomain());\n")
        .append("         cookieArray[i++] = cookie;\n")
        .append("      }\n")
        .append("      return cookieArray;\n")
        .append("   }\n\n")
//        .append("   private static org_jboss_resteasy_plugins_grpc_sse___SseEvent transformSseEvent(ByteArrayInputStream bais) throws IOException {\n")
//        .append("      SseEventInputImpl eventInput = new SseEventInputImpl(null, MediaType.TEXT_PLAIN_TYPE, null, null, bais);\n")
//        .append("      InboundSseEventImpl inboundEvent = (InboundSseEventImpl) eventInput.read();\n")
//        .append("      org_jboss_resteasy_plugins_grpc_sse___SseEvent.Builder builder = org_jboss_resteasy_plugins_grpc_sse___SseEvent.newBuilder();\n")
//        .append("      builder.setComment(inboundEvent.getComment());\n")
//        .append("      builder.setData(ByteString.copyFrom(inboundEvent.getRawData()));\n")
//        .append("      builder.setId(inboundEvent.getId());\n")
//        .append("      builder.setName(inboundEvent.getName());\n")
//        .append("      builder.setReconnectDelay(inboundEvent.getReconnectDelay());\n")
//        .append("      return builder.build();\n")
//        .append("   }\n")
        ;
   }

   private static String getParamType(String packageName, String outerClassName, String param) {
      return packageName + "." + outerClassName + ".GeneralEntityMessage";
   }
   
   private static String getReturnType(String packageName, String outerClassName, String param) {
      int pos = param.indexOf("stream");
      if (pos >= 0) {
         param = param.substring(pos + 6).stripLeading();
      }
      if ("google.protobuf.Any".equals(param)) {
         return "com.google.protobuf.Any";
      }
      return packageName + "." + outerClassName + "." + param;
   }

   private String getGetterMethod(String actualEntityClass) {
      actualEntityClass = actualEntityClass.replaceAll("___", "_");
      StringBuilder sb = new StringBuilder("get");
      sb.append(actualEntityClass.substring(0, 1).toUpperCase());
      for (int i = 1; i < actualEntityClass.length(); ) {
         if ("_".equals(actualEntityClass.substring(i, i + 1))) {
            sb.append(actualEntityClass.substring(i + 1, i + 2).toUpperCase());
            i += 2;
         } else {
            sb.append(actualEntityClass.charAt(i++));
         }
      }
      sb.append("Field()");
      return sb.toString();
   }
   
   private void writeClass(StringBuilder sbHeader, StringBuilder sbBody) throws IOException {
      String path = "";
      for (String s : ("target/generated-sources/protobuf/grpc-java/" + packageName.replace(".", "/")).split("/")) {
         path += s;
         File dir = new File(path);
         if(!dir.exists()){
            dir.mkdir();
         }
         path += "/";
      }
      File file = new File(path + serviceName + "GrpcImpl.java");
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sbHeader.toString());
      bw.write(sbBody.toString());
      bw.close();
   }
}
