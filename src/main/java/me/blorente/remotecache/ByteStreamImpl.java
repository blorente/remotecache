package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import com.google.bytestream.ByteStreamGrpc;
import com.google.bytestream.ByteStreamProto;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteStreamImpl extends ByteStreamGrpc.ByteStreamImplBase {
    private static final Logger logger = Logger.getLogger(ByteStreamImpl.class.getName());

    private final CacheStorage storage;

    public ByteStreamImpl(CacheStorage storage) {
        this.storage = storage;
    }

    @Override
    public void read(ByteStreamProto.ReadRequest request, StreamObserver<ByteStreamProto.ReadResponse> responseObserver) {
        logger.info(String.format("BL: I got ByteStream.read request %s", request));
    }

    @Override
    public StreamObserver<ByteStreamProto.WriteRequest> write(StreamObserver<ByteStreamProto.WriteResponse> responseObserver) {
        return new StreamObserver<ByteStreamProto.WriteRequest>() {

            @Override
            public void onNext(ByteStreamProto.WriteRequest request) {
                // TODO BL: handle request.getFinishWrite()
                logger.info(String.format("BL: I got ByteStream.write request %s", request.getResourceName()));
                String resourceName = request.getResourceName();
                Digest digest = parseDigestFromResourceName(resourceName);
                ByteString data = request.getData();
                storage.cas().put(digest, data);
                responseObserver.onNext(ByteStreamProto.WriteResponse.newBuilder().setCommittedSize(digest.getSizeBytes()).build());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.SEVERE, "BL: There was an error while writing", t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void queryWriteStatus(ByteStreamProto.QueryWriteStatusRequest request, StreamObserver<ByteStreamProto.QueryWriteStatusResponse> responseObserver) {
        logger.info(String.format("BL: I got ByteStream.queryWriteStatus request %s", request));
        Digest requestedDigest = parseDigestFromResourceName(request.getResourceName());

        // TODO BL: We should also set .setCommittedSize
        if (storage.cas().containsKey(requestedDigest)) {
            responseObserver.onNext(ByteStreamProto.QueryWriteStatusResponse.newBuilder().setComplete(true).build());
        } else {
            responseObserver.onNext(ByteStreamProto.QueryWriteStatusResponse.newBuilder().setComplete(false).build());
        }
        responseObserver.onCompleted();
    }

    // TODO BL: error check this parsing
    private Digest parseDigestFromResourceName(String resourceName) {
        String digestRaw = resourceName.split("/blobs/")[1];
        String[] digestParts = digestRaw.split("/");
        String hash = digestParts[0];
        long size = Long.parseLong(digestParts[1]);
        Digest ret = Digest.newBuilder().setHash(hash).setSizeBytes(size).build();
        logger.info(String.format("BL: Parsed resource %s into digest %s", resourceName, ret));
        return ret;
    }
}
