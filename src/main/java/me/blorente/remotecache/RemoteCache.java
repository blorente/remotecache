package me.blorente.remotecache;

import io.grpc.*;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@CommandLine.Command(
    name = "bl-remote-cache",
    description = "Remote gRPC server implementing the remote cache from bazelbuild/remote-apis")
public class RemoteCache implements Callable<Integer> {
  private static final Logger logger = Logger.getLogger(RemoteCache.class.getName());

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to serve traffic on")
  private int port = 5001;

  @CommandLine.Option(
      names = {"--max-blobs"},
      description = "Capacity for the CAS in number of blobs")
  private int maxBlobs = 10000;

  @CommandLine.Option(
      names = {"--max-actions"},
      description = "Capacity for the Action Cache in number of blobs")
  private int maxActions = 10000;

  @CommandLine.Option(
      names = {"--debug-grpc"},
      description = "Print every gRPC request received")
  private boolean debugGrpc = false;

  private Server server;

  private void start() throws IOException {
    CacheStorage storage = new CacheStorage(maxBlobs, maxActions);
//    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(this.port);
//    if (debugGrpc) {
//      serverBuilder = serverBuilder.intercept(new GrpcDebugInterceptor());
//    }
//    serverBuilder =
//        serverBuilder
//            .addService(new CASImpl(storage))
//            .addService(new ACImpl(storage))
//            .addService(new ByteStreamImpl(storage))
//            .addService(new CapabilitiesImpl());
//    server = serverBuilder.build().start();
  server = ServerBuilder.forPort(this.port)
            .addService(new CASImpl(storage))
            .addService(new ACImpl(storage))
            .addService(new ByteStreamImpl(storage))
            .addService(new CapabilitiesImpl())
          .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
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

  @Override
  public Integer call() throws Exception {
    this.start();
    this.blockUntilShutdown();
    return 0;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    int exitCode = new CommandLine(new RemoteCache()).execute(args);
    System.exit(exitCode);
  }

  private static class GrpcDebugInterceptor implements ServerInterceptor {
    private static final Logger logger = Logger.getLogger(GrpcDebugInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
      logger.info(String.format("got gRPC request %s", call.getMethodDescriptor().getFullMethodName()));
      return next.startCall(call, headers);
    }
  }
}
