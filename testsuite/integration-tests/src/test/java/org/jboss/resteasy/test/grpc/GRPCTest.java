package org.jboss.resteasy.test.grpc;

import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.grpc.resource.GRPCApplication;
import org.jboss.resteasy.test.grpc.resource.GRPCResource;
import org.jboss.resteasy.test.grpc.resource.GreeterGrpc;
import org.jboss.resteasy.test.grpc.resource.HelloReply;
import org.jboss.resteasy.test.grpc.resource.HelloReplyOrBuilder;
import org.jboss.resteasy.test.grpc.resource.HelloRequest;
import org.jboss.resteasy.test.grpc.resource.HelloWorldClient;
import org.jboss.resteasy.test.grpc.resource.HelloWorldProto;
import org.jboss.resteasy.test.grpc.resource.HelloWorldServer_gRPC;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * @tpSubChapter gRPC integration tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.13.0.Final
 * @tpTestCaseDetails 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GRPCTest
{
   private static String target = "localhost:8081";
   private static GreeterGrpc.GreeterBlockingStub blockingStub;
   
   private static ManagedChannel channel;
   
   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(GRPCTest.class.getSimpleName());
      war.addClass(GreeterGrpc.class);
//      war.addClass(GRPCApplication.class);
      war.addClass(HelloReply.class);
      war.addClass(HelloReplyOrBuilder.class);
      war.addClass(HelloReply.class);
      war.addClass(HelloReplyOrBuilder.class);
      war.addClass(HelloWorldProto.class);
      war.addClass(HelloWorldServer_gRPC.class);
      war.addClass(HelloWorldClient.class);
      return TestUtil.finishContainerPrepare(war, null, GRPCResource.class);
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      System.out.println("entered beforeClass()");
//      HelloWorldServer_gRPC.main(null);
//      System.out.println("called main()");
//      // Create a communication channel to the server, known as a Channel. Channels are thread-safe
//      // and reusable. It is common to create channels at the beginning of your application and reuse
//      // them until the application shuts down.
      ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
      System.out.println("created channel");
      blockingStub = GreeterGrpc.newBlockingStub(channel);
      System.out.println("created blockingStub");
      System.out.println("finished beforeClass()");
   }

//   @AfterClass
   public static void afterClass() throws InterruptedException
   {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
   }

   /**
    * @tpTestDetails Test gRPC.
    * @tpSince RESTEasy 3.13.0.Final
    */
   @Test
   public void testGRPC() throws Exception
   {
      HelloRequest request = HelloRequest.newBuilder().setName("bill").build();
      HelloReply response;
      try {
         response = blockingStub.sayHello(request);
         System.out.println("response: " + response.getMessage());
         Assert.assertEquals("hello bill", response.getMessage());
         Assert.fail("ok");
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }
}
