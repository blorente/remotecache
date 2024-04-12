package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import build.bazel.semver.SemVer;
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

        ServerCapabilities capabilities = ServerCapabilities
                        .newBuilder()
                        .setLowApiVersion(SemVer.newBuilder().setMajor(2).build())
                        .setCacheCapabilities(cacheCapabilities)
                        .build();
        System.out.println(String.format("BL: Server capabilities: %s", capabilities));
        responseObserver.onNext(
                capabilities
        );
        responseObserver.onCompleted();
    }
}
