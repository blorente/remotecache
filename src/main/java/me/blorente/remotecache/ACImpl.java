package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionCacheGrpc;
import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.GetActionResultRequest;
import build.bazel.remote.execution.v2.UpdateActionResultRequest;
import build.bazel.remote.execution.v2.Digest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ACImpl extends ActionCacheGrpc.ActionCacheImplBase {
  private static final Logger logger = Logger.getLogger(ACImpl.class.getName());

  private final CacheStorage storage;

  public ACImpl(CacheStorage storage) {
    this.storage = storage;
  }

  @Override
  public void getActionResult(
      GetActionResultRequest request, StreamObserver<ActionResult> responseObserver) {
//    logger.info(String.format("BL: I got getActionResult request %s", request));
    Digest digest = request.getActionDigest();
    if (storage.ac().containsKey(digest)) {
      ActionResult res = storage.ac().get(digest);
      logger.info(String.format("BL: Action found %s", digest));
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    } else {
      logger.info("BL: Action Not Found. Answering with NOT_FOUND");
      responseObserver.onError(
          Status.Code.NOT_FOUND
              .toStatus()
              .augmentDescription("Action not found in the cache")
              .asException());
    }
  }

  @Override
  public void updateActionResult(
      UpdateActionResultRequest request, StreamObserver<ActionResult> responseObserver) {
    logger.info(String.format("BL: I got getActionResult request %s", request));
    Digest digest = request.getActionDigest();
    ActionResult result = request.getActionResult();
    storage.ac().put(digest, result);
    responseObserver.onNext(result);
    responseObserver.onCompleted();
  }
}
