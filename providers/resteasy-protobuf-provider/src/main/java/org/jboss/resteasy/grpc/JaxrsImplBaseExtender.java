package org.jboss.resteasy.grpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import org.jboss.logging.Logger;

public class JaxrsImplBaseExtender {

   private static Logger logger = Logger.getLogger(JaxrsImplBaseExtender.class);

   private String packageName = "";
   private String outerClassName = "";
   private String serviceName = "";
   private String servletName = "";

   public static void main(String[] args) {
      if (args.length != 2) {
         logger.info("need two args:");
         logger.info("  arg[0]: .proto file prefix");
         logger.info("  arg[1]: servlet name");
         return;
      }
      new JaxrsImplBaseExtender(args);
   }

   public JaxrsImplBaseExtender(String[] args) {
      servletName = args[1];
      parse(args[0]);
   }

   private void parse(String fileName) {
      File file = new File("./src/main/proto/" + fileName + ".proto");
      if (!file.exists()) {
         throw new RuntimeException(fileName + ".proto not found");
      }
      try {
         StringBuilder sbHeader = new StringBuilder();
         StringBuilder sbBody = new StringBuilder();
         Reader reader = new FileReader(file);
         Scanner scanner = new Scanner(reader);
         classHeader(scanner, sbHeader);
         String s = scanner.findWithinHorizon("service ", 0);
         while (s != null) {
            serviceName = scanner.next();
            sbHeader.append("import ")
                    .append(packageName).append(".")
                    .append(serviceName).append("Grpc").append(".")
                    .append(serviceName).append("ImplBase;\n");
            service(scanner, sbHeader, sbBody);
            s = scanner.findWithinHorizon("service ", 0);
         }
         staticMethods(sbBody);
         sbBody.append("}\n");
         writeClass(sbHeader, sbBody);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void classHeader(Scanner scanner, StringBuilder sb) {
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
      imports(scanner, sb);
      scanner.reset();
   }

   private void imports(Scanner scanner, StringBuilder sb) {
      sb.append("import com.google.protobuf.GeneratedMessageV3;\n")
        .append("import io.grpc.stub.StreamObserver;\n")
        .append("import java.io.ByteArrayInputStream;\n")
        .append("import java.io.ByteArrayOutputStream;\n")
        .append("import java.lang.reflect.Proxy;\n")
        .append("import javax.servlet.Servlet;\n")
        .append("import javax.servlet.http.HttpServletRequest;\n")
        .append("import javax.servlet.http.HttpServletResponse;\n")
        .append("import org.jboss.resteasy.core.ResteasyContext;\n")
        .append("import io.grpc.classes.HttpServletRequestHandler;\n")
        .append("import io.grpc.classes.HttpServletResponseHandler;\n")
        .append("import io.grpc.classes.MockServletOutputStream;\n");
   }

   private void service(Scanner scanner, StringBuilder sbHeader, StringBuilder sbBody) {
      sbBody.append("public class ")
            .append(serviceName)
            .append("GrpcImpl extends ")
            .append(serviceName)
            .append("ImplBase {\n");
      String rpc = scanner.findWithinHorizon(" rpc ", 0);
      while (rpc != null) {
         rpc(scanner, sbHeader, sbBody);
         rpc = scanner.findWithinHorizon(" rpc ", 0);
      }
   }

   private void rpc(Scanner scanner, StringBuilder sbHeader, StringBuilder sbBody) {
      sbBody.append("\n   @java.lang.Override\n");
      String method = scanner.next();
      scanner.findWithinHorizon("\\(", 0);
      scanner.useDelimiter("\\)");
      String param = getType(packageName, outerClassName, scanner.next());
      scanner.findWithinHorizon("returns", 0);
      scanner.findWithinHorizon("\\(", 0);
      String retn = getType(packageName, outerClassName, scanner.next());
      sbBody.append("   public void ")
      .append(method).append("(")
      .append(param).append(" param, ")
      .append("StreamObserver<").append(retn).append("> responseObserver) {\n");
      rpcBody(scanner, sbBody, retn);
      sbBody.append("   }\n");
      scanner.reset();
   }

   private void rpcBody(Scanner scanner, StringBuilder sb, String retn) {
      sb.append("      try {\n")
        .append("         HttpServletResponse response = getHttpServletResponse();\n")
        .append("         Servlet servlet = ResteasyContext.getServlet(\"").append(servletName).append("\");\n") // plug in correct servlet
        .append("         servlet.service(getHttpServletRequest(param), response);\n")
        .append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
        .append("         ByteArrayOutputStream baos = msos.getDelegate();\n")
        .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());\n")
        .append("         ").append(retn).append(" reply = ").append(retn).append(".parseFrom(bais);\n")
        .append("         responseObserver.onNext(reply);\n")
        .append("      } catch (Exception e) {\n")
        .append("         responseObserver.onError(e);\n")
        .append("         return;\n")
        .append("      }\n")
        .append("      responseObserver.onCompleted();\n");
   }

   private static void staticMethods(StringBuilder sb) {
      sb.append("\n")
        .append("   private static HttpServletRequest getHttpServletRequest(GeneratedMessageV3 message) {\n")
        .append("      return (HttpServletRequest) Proxy.newProxyInstance(\n")
        .append("         HttpServletRequest.class.getClassLoader(),\n")
        .append("         new Class[] { HttpServletRequest.class },\n")
        .append("         new HttpServletRequestHandler(\"HelloWorldProto.Greeter/sayHello\", message));\n")
        .append("   }\n\n");
      sb.append("   private static HttpServletResponse getHttpServletResponse() {\n")
        .append("      return (HttpServletResponse) Proxy.newProxyInstance(\n")
        .append("         HttpServletResponse.class.getClassLoader(),\n")
        .append("         new Class[] { HttpServletResponse.class },\n")
        .append("         new HttpServletResponseHandler());\n")
        .append("   }\n\n");
   }

   private static String getType(String packageName, String outerClassName, String param) {
      return packageName + "." + outerClassName + "." + param;
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
