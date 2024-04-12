package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ContentAddressableStorageGrpc;
import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.FindMissingBlobsRequest;
import build.bazel.remote.execution.v2.FindMissingBlobsResponse;
import build.bazel.remote.execution.v2.BatchReadBlobsRequest;
import build.bazel.remote.execution.v2.BatchUpdateBlobsRequest;
import build.bazel.remote.execution.v2.BatchReadBlobsResponse;
import build.bazel.remote.execution.v2.BatchUpdateBlobsResponse;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import java.util.List;
import java.util.logging.Logger;

public class CASImpl extends ContentAddressableStorageGrpc.ContentAddressableStorageImplBase {
  private static final Logger logger = Logger.getLogger(CASImpl.class.getName());

  private final CacheStorage storage;

  public CASImpl(CacheStorage storage) {
    this.storage = storage;
  }

  @Override
  public void findMissingBlobs(
      FindMissingBlobsRequest request, StreamObserver<FindMissingBlobsResponse> responseObserver) {
    List<Digest> digests = request.getBlobDigestsList();
    FindMissingBlobsResponse.Builder response = FindMissingBlobsResponse.newBuilder();
    for (Digest digest : digests) {
      if (!storage.hasBlob(digest)) {
        response.addMissingBlobDigests(digest);
      }
    }
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  @Override
  public void batchUpdateBlobs(
      BatchUpdateBlobsRequest request, StreamObserver<BatchUpdateBlobsResponse> responseObserver) {
    List<BatchUpdateBlobsRequest.Request> updateRequests = request.getRequestsList();
    for (BatchUpdateBlobsRequest.Request req : updateRequests) {
      Digest digest = req.getDigest();
      ByteString data = req.getData();
      storage.writeBlob(digest, data);
    }
    responseObserver.onCompleted();
  }

  @Override
  public void batchReadBlobs(
      BatchReadBlobsRequest request, StreamObserver<BatchReadBlobsResponse> responseObserver) {
    BatchReadBlobsResponse.Builder responseBuilder = BatchReadBlobsResponse.newBuilder();
    for (Digest digest : request.getDigestsList()) {
      ByteString data = storage.getBlob(digest);
      BatchReadBlobsResponse.Response resp =
          BatchReadBlobsResponse.Response.newBuilder().setDigest(digest).setData(data).build();
      responseBuilder.addResponses(resp);
    }
    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }
}
