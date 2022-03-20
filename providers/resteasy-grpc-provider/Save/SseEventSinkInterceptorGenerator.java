package org.jboss.resteasy.plugins.protobuf.sse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.logging.Logger;

public class SseEventSinkInterceptorGenerator {
   
   private static Logger logger = Logger.getLogger(SseEventSinkInterceptorGenerator.class);
   private static final String SSE_EVENT_SINK_INTERCEPTOR = 
         "package org.jboss.resteasy.plugins.protobuf.sse;\n"
         + "\n"
         + "import java.io.IOException;\n"
         + "import java.util.Map;\n"
         + "\n"
         + "import javax.annotation.Priority;\n"
         + "import javax.ws.rs.container.ContainerRequestContext;\n"
         + "import javax.ws.rs.container.ContainerRequestFilter;\n"
         + "import javax.ws.rs.ext.Provider;\n"
         + "import javax.ws.rs.sse.SseEventSink;\n"
         + "\n"
         + "import jaxrs.example.CC1MessageBodyReaderWriter;\n"
         + "import org.jboss.resteasy.core.PostResourceMethodInvoker;\n"
         + "import org.jboss.resteasy.core.PostResourceMethodInvokers;\n"
         + "import org.jboss.resteasy.core.ResourceMethodInvoker;\n"
         + "import org.jboss.resteasy.core.ResteasyContext;\n"
         + "import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;\n"
         + "import org.jboss.resteasy.plugins.protobuf.sse.SseReaderWriter;\n"
         + "import org.jboss.resteasy.plugins.providers.sse.SseEventOutputImpl;\n"
         + "import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;\n"
         + "import org.jboss.resteasy.spi.Dispatcher;\n"
         + "import org.jboss.resteasy.spi.ResteasyProviderFactory;\n"
         + "\n"
         + "@Provider\n"
         + "@Priority(Integer.MAX_VALUE - 1)\n"
         + "public class %sSseEventSinkInterceptor implements ContainerRequestFilter\n"
         + "{\n"
         + "   @Override\n"
         + "   public void filter(ContainerRequestContext requestContext) throws IOException\n"
         + "   {\n"
         + "      System.out.println(\"entering ...SseEventSinkInterceptor.filter()\");\n"
         + "      Map<Class<?>, Object> context = ResteasyContext.getContextDataMap();\n"
         + "      ResourceMethodInvoker rmi = ((PostMatchContainerRequestContext) requestContext).getResourceMethod();\n"
         + "      if (rmi.isAsyncStreamProvider() || rmi.isSse())\n"
         + "      {\n"
         + "         Dispatcher dispatcher = ResteasyContext.getContextData(Dispatcher.class);\n"
         + "         ResteasyProviderFactory providerFactory = dispatcher != null ? dispatcher.getProviderFactory() : ResteasyProviderFactory.getInstance();\n"
         + "         SseEventOutputImpl sink = (SseEventOutputImpl) ResteasyContext.getContextDataMap().get(SseEventSink.class);\n"
         + "         if (sink != null) {\n"
         + "            ResteasyContext.getContextDataMap().remove(sink);\n"
         + "            System.out.println(this + \" removed \" + sink);\n"
         + "         }\n"
         + "         System.out.println(\"sink now 1: \" + ResteasyContext.getContextDataMap().get(SseEventSink.class));\n"
         + "         final SseEventOutputImpl finalSink = new SseEventOutputImpl(new SseReaderWriter(new %s(), providerFactory), providerFactory);\n"
         + "         ResteasyContext.getContextDataMap().put(SseEventSink.class, finalSink);\n"
         + "         System.out.println(\"sink now 2: \" + ResteasyContext.getContextDataMap().get(SseEventSink.class));\n"
//         + "         ResteasyContext.getContextData(PostResourceMethodInvokers.class).addInvokers(new PostResourceMethodInvoker()\n"
//         + "         {\n"
//         + "            @Override\n"
//         + "            public void invoke()\n"
//         + "            {\n"
//         + "               finalSink.flushResponseToClient();\n"
//         + "            }\n"
//         + "         });\n"
         + "      }\n"
         + "   }\n"
         + "}";

   public static void main(String[] args) {
      if (args.length != 2) {
         logger.info("need two args:");
         logger.info("  arg[0]: root class name");
         logger.info("  arg[1]: javabuf wrapper class name");
         return;
      }
      String interceptorClassName = args[0] + "SseEventSinkInterceptor";
      String interceptorClass = String.format(SSE_EVENT_SINK_INTERCEPTOR, args[0], args[0] + "MessageBodyReaderWriter");
//      try {
//         writeInterceptorClass(args, interceptorClassName, interceptorClass);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
   }

   private static void writeInterceptorClass(String[] args, String interceptorClassName, String interceptorClass) throws IOException  {
      StringBuilder root = new StringBuilder("target/generated-sources/protobuf/grpc-java/");
      File dir = new File(root.toString());
      if(!dir.exists()) {
         dir.mkdir();
      }
      String pkg = args[1].lastIndexOf(".") < 0 ? "" : args[1].substring(0, args[1].lastIndexOf("."));
      for (String s : pkg.split("\\.")) {
         dir = new File(root.append("/").append(s).toString());
         if (!dir.exists())
         {
            dir.mkdir();
         }
      }
      File file = new File(root.append("/").append(interceptorClassName).append(".java").toString());
      if (!file.exists()) {
         file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(interceptorClass);
      bw.close();
   }
}
