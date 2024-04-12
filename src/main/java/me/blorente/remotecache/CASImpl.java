package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ContentAddressableStorageGrpc;
import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.FindMissingBlobsRequest;
import build.bazel.remote.execution.v2.FindMissingBlobsResponse;
import build.bazel.remote.execution.v2.BatchReadBlobsRequest;
import build.bazel.remote.execution.v2.BatchUpdateBlobsRequest;
import build.bazel.remote.execution.v2.BatchReadBlobsResponse;
import build.bazel.remote.execution.v2.BatchUpdateBlobsResponse;
import build.bazel.remote.execution.v2.GetTreeRequest;
import build.bazel.remote.execution.v2.GetTreeResponse;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class CASImpl extends ContentAddressableStorageGrpc.ContentAddressableStorageImplBase {
  private static final Logger logger = Logger.getLogger(CASImpl.class.getName());

  private final ConcurrentMap<Digest, ByteString> blobs = new ConcurrentHashMap<>();

  @Override
  public void findMissingBlobs(
      FindMissingBlobsRequest request, StreamObserver<FindMissingBlobsResponse> responseObserver) {
    logger.info(String.format("BL: I got findMissingBlobs request %s", request));
    logger.info(String.format("BL: Blobs are %s", blobs));
    List<Digest> digests = request.getBlobDigestsList();
    FindMissingBlobsResponse.Builder response = FindMissingBlobsResponse.newBuilder();
    for (Digest digest : digests) {
      if (!blobs.containsKey(digest)) {
        response.addMissingBlobDigests(digest);
      }
    }
    logger.info(String.format("BL: Not found blobs are %s", response.getMissingBlobDigestsList()));
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void batchUpdateBlobs(
      BatchUpdateBlobsRequest request, StreamObserver<BatchUpdateBlobsResponse> responseObserver) {
    logger.info(String.format("BL: I got batchUpdateBlobs request %s", request));
    List<BatchUpdateBlobsRequest.Request> updateRequests = request.getRequestsList();
    for (BatchUpdateBlobsRequest.Request req : updateRequests) {
      Digest digest = req.getDigest();
      ByteString data = req.getData();
      blobs.put(digest, data);
    }
    responseObserver.onCompleted();
  }

  @Override
  public void batchReadBlobs(
      BatchReadBlobsRequest request, StreamObserver<BatchReadBlobsResponse> responseObserver) {
    logger.info(String.format("BL: I got batchReadBlobs request %s", request));
    BatchReadBlobsResponse.Builder responseBuilder = BatchReadBlobsResponse.newBuilder();
    for (Digest digest : request.getDigestsList()) {
      ByteString data = blobs.get(digest);
      BatchReadBlobsResponse.Response resp =
          BatchReadBlobsResponse.Response.newBuilder().setDigest(digest).setData(data).build();
      responseBuilder.addResponses(resp);
    }
    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void getTree(GetTreeRequest request, StreamObserver<GetTreeResponse> responseObserver) {
    logger.info(String.format("BL: I got getTree request %s", request));
  }
}
