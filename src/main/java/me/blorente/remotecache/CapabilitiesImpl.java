package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import build.bazel.semver.SemVer;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class CapabilitiesImpl extends CapabilitiesGrpc.CapabilitiesImplBase {
    private static final Logger logger = Logger.getLogger(CapabilitiesImpl.class.getName());

    @Override
    public void getCapabilities(GetCapabilitiesRequest request, StreamObserver<ServerCapabilities> responseObserver) {
        CacheCapabilities cacheCapabilities = CacheCapabilities.newBuilder()

                .addDigestFunctions(DigestFunction.Value.SHA256)
                .addSupportedBatchUpdateCompressors(Compressor.Value.IDENTITY)
                .addSupportedCompressors(Compressor.Value.IDENTITY)
                .setSymlinkAbsolutePathStrategy(SymlinkAbsolutePathStrategy.Value.DISALLOWED)
                .setActionCacheUpdateCapabilities(
                        ActionCacheUpdateCapabilities.newBuilder().setUpdateEnabled(true)
                )
                .setMaxBatchTotalSizeBytes(4 * 1024 * 1024)
                .build();

        SemVer supportedVersion = SemVer.newBuilder().setMajor(2).build();
        ServerCapabilities capabilities = ServerCapabilities
                        .newBuilder()
                        .setLowApiVersion(supportedVersion)
                        .setHighApiVersion(supportedVersion)
                        .setCacheCapabilities(cacheCapabilities)
                        .build();
        logger.info(String.format("Server capabilities: %s", capabilities));
        responseObserver.onNext(
                capabilities
        );
        responseObserver.onCompleted();
    }
}
