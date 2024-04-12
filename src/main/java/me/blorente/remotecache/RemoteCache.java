package me.blorente.remotecache;

import io.grpc.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class RemoteCache {
    private static final Logger logger = Logger.getLogger(RemoteCache.class.getName());

    private Server server;

    private void start() throws IOException {
      int port = 50051;
      server = ServerBuilder.forPort(port)
              .intercept(new GrpcRequestPrinter())
          .addService(new CASImpl())
          .addService(new ACImpl())
          .addService(new CapabilitiesImpl())
          .build()
          .start();
      logger.info("Server started, listening on " + port);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          // Use stderr here since the logger may have been reset by its JVM shutdown hook.
          System.err.println("*** shutting down gRPC server since JVM is shutting down");
          try {
            RemoteCache.this.stop();
          } catch (InterruptedException e) {
            e.printStackTrace(System.err);
          }
          System.err.println("*** server shut down");
        }
      });
    }

    
    private void stop() throws InterruptedException {
      if (server != null) {
        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
      }
    }

    private void blockUntilShutdown() throws InterruptedException {
      if (server != null) {
        server.awaitTermination();
      }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final RemoteCache server = new RemoteCache();
        server.start();
        server.blockUntilShutdown();
    }

    private class GrpcRequestPrinter implements ServerInterceptor {
        private static final Logger logger = Logger.getLogger(GrpcRequestPrinter.class.getName());

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            logger.info(String.format("BL: grpc call method=%s headers=%s", call.getMethodDescriptor().getFullMethodName(), headers));
            return next.startCall(call, headers);
        }
    }
}
