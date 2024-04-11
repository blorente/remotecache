package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import io.grpc.stub.StreamObserver;

public class CapabilitiesImpl extends CapabilitiesGrpc.CapabilitiesImplBase {
    @Override
    public void getCapabilities(GetCapabilitiesRequest request, StreamObserver<ServerCapabilities> responseObserver) {
        CacheCapabilities cacheCapabilities = CacheCapabilities.newBuilder()
                .addDigestFunctions(DigestFunction.Value.SHA256)
                .setActionCacheUpdateCapabilities(
                        ActionCacheUpdateCapabilities.newBuilder().setUpdateEnabled(true)
                )
                .setMaxBatchTotalSizeBytes(4 * 1024 * 1024)
                .build();
        System.out.println(String.format("BL: Cache capabilities: %s", cacheCapabilities));
        responseObserver.onNext(
                ServerCapabilities
                        .newBuilder()
                        .setCacheCapabilities(cacheCapabilities)
                        .build()
        );
    }
}
