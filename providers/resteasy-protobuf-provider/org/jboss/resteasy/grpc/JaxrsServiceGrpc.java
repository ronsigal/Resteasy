package org.jboss.resteasy.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: jaxrs.proto")
public final class JaxrsServiceGrpc {

  private JaxrsServiceGrpc() {}

  public static final String SERVICE_NAME = "org.jboss.resteasy.grpc.JaxrsService";

  // Static method descriptors that strictly reflect the proto.
  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JaxrsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceStub>() {
        @java.lang.Override
        public JaxrsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JaxrsServiceStub(channel, callOptions);
        }
      };
    return JaxrsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JaxrsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceBlockingStub>() {
        @java.lang.Override
        public JaxrsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JaxrsServiceBlockingStub(channel, callOptions);
        }
      };
    return JaxrsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JaxrsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JaxrsServiceFutureStub>() {
        @java.lang.Override
        public JaxrsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JaxrsServiceFutureStub(channel, callOptions);
        }
      };
    return JaxrsServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class JaxrsServiceImplBase implements io.grpc.BindableService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .build();
    }
  }

  /**
   */
  public static final class JaxrsServiceStub extends io.grpc.stub.AbstractAsyncStub<JaxrsServiceStub> {
    private JaxrsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JaxrsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JaxrsServiceStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class JaxrsServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<JaxrsServiceBlockingStub> {
    private JaxrsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JaxrsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JaxrsServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class JaxrsServiceFutureStub extends io.grpc.stub.AbstractFutureStub<JaxrsServiceFutureStub> {
    private JaxrsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JaxrsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JaxrsServiceFutureStub(channel, callOptions);
    }
  }


  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final JaxrsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(JaxrsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class JaxrsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JaxrsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.jboss.resteasy.grpc.JaxrsService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JaxrsService");
    }
  }

  private static final class JaxrsServiceFileDescriptorSupplier
      extends JaxrsServiceBaseDescriptorSupplier {
    JaxrsServiceFileDescriptorSupplier() {}
  }

  private static final class JaxrsServiceMethodDescriptorSupplier
      extends JaxrsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    JaxrsServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (JaxrsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JaxrsServiceFileDescriptorSupplier())
              .build();
        }
      }
    }
    return result;
  }
}
