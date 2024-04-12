package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import com.google.bytestream.ByteStreamGrpc;
import com.google.bytestream.ByteStreamProto;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteStreamImpl extends ByteStreamGrpc.ByteStreamImplBase {
  private static final Logger logger = Logger.getLogger(ByteStreamImpl.class.getName());

  private final CacheStorage storage;

  public ByteStreamImpl(CacheStorage storage) {
    this.storage = storage;
  }

  @Override
  public void read(
      ByteStreamProto.ReadRequest request,
      StreamObserver<ByteStreamProto.ReadResponse> responseObserver) {
    String resourceName = request.getResourceName();
    DigestParseResult parseResult = parseDigestFromResourceName(resourceName);
    if (parseResult.isError()) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT.augmentDescription(parseResult.error()).asException());
      return;
    }
    Digest digest = parseResult.digest();
    if (storage.hasBlob(digest)) {
      ByteString data = storage.getBlob(digest);
      responseObserver.onNext(ByteStreamProto.ReadResponse.newBuilder().setData(data).build());
      responseObserver.onCompleted();
    } else {
      responseObserver.onError(
          Status.NOT_FOUND
              .augmentDescription(String.format("Item %s not found in cache", resourceName))
              .asException());
    }
  }

  @Override
  public StreamObserver<ByteStreamProto.WriteRequest> write(
      StreamObserver<ByteStreamProto.WriteResponse> responseObserver) {
    return new StreamObserver<ByteStreamProto.WriteRequest>() {
      private Digest currentDigest = null;
      private ByteString currentData = ByteString.EMPTY;

      @Override
      public void onNext(ByteStreamProto.WriteRequest request) {
        logger.info(
            String.format("BL: I got ByteStream.write request %s", request.getResourceName()));

        Digest digest = currentDigest;
        String resourceName = request.getResourceName();
        if (!resourceName.isEmpty()) {
          DigestParseResult parseResult = parseDigestFromResourceName(resourceName);
          if (parseResult.isError()) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.augmentDescription(parseResult.error()).asException());
            return;
          }
          if (currentDigest == null) {
            currentDigest = parseResult.digest;
          } else if (digest != parseResult.digest) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .augmentDescription(
                        String.format(
                            "Mismatching digests for resource names. Currently writing %s, got %s",
                            digest, parseResult.digest))
                    .asException());
            return;
          }
        }

        ByteString data = request.getData();
        currentData = currentData.concat(data);

        boolean finish = request.getFinishWrite();
        if (finish) {
          storage.writeBlob(currentDigest, currentData);
          responseObserver.onNext(
                  ByteStreamProto.WriteResponse.newBuilder()
                          .setCommittedSize(currentData.size())
                          .build());
          currentDigest = null;
          currentData = ByteString.EMPTY;
        }
      }

      @Override
      public void onError(Throwable t) {
        logger.log(Level.SEVERE, "There was an error while writing blob", t);
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public void queryWriteStatus(
      ByteStreamProto.QueryWriteStatusRequest request,
      StreamObserver<ByteStreamProto.QueryWriteStatusResponse> responseObserver) {

    DigestParseResult parseResult = parseDigestFromResourceName(request.getResourceName());
    if (parseResult.isError()) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT.augmentDescription(parseResult.error()).asException());
      return;
    }
    Digest requestedDigest = parseResult.digest();

    if (storage.hasBlob(requestedDigest)) {
      responseObserver.onNext(
          ByteStreamProto.QueryWriteStatusResponse.newBuilder().setComplete(true).build());
    } else {
      responseObserver.onNext(
          ByteStreamProto.QueryWriteStatusResponse.newBuilder().setComplete(false).build());
    }
    responseObserver.onCompleted();
  }

  private record DigestParseResult(@Nullable Digest digest, @Nullable String error) {
    public boolean isError() {
      return this.error != null;
    }
  }

  private DigestParseResult parseDigestFromResourceName(String resourceName) {
    logger.info(String.format("BL: parseDigestFromResourceName resourceName %s", resourceName));
    String[] resourceNameParts = resourceName.split("/blobs/");
    if (resourceNameParts.length != 2) {
      return new DigestParseResult(
          null,
          String.format(
              "Expected resourceName to have exactly one /blobs/ section, but got %s",
              resourceName));
    }
    String digestRaw = resourceNameParts[1];
    String[] digestParts = digestRaw.split("/");
    if (digestParts.length != 2) {
      return new DigestParseResult(
          null, String.format("Expected digest to have exactly one /, but got %s", digestRaw));
    }
    String hash = digestParts[0];
    try {
      long size = Long.parseLong(digestParts[1]);
      Digest ret = Digest.newBuilder().setHash(hash).setSizeBytes(size).build();
      return new DigestParseResult(ret, null);
    } catch (NumberFormatException e) {
      return new DigestParseResult(
          null, String.format("Could not convert %s to a number", digestParts[1]));
    }
  }
}
