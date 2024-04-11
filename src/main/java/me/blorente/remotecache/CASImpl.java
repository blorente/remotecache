package me.blorente.remotecache;

import build.bazel.remote.execution.v2.*;
import io.grpc.stub.StreamObserver;

public class CASImpl extends ContentAddressableStorageGrpc.ContentAddressableStorageImplBase {
    @Override
    public void findMissingBlobs(FindMissingBlobsRequest request, StreamObserver<FindMissingBlobsResponse> responseObserver) {
        System.out.println(String.format("BL: I got findMissingBlobs request %s", request));
    }

    @Override
    public void batchUpdateBlobs(BatchUpdateBlobsRequest request, StreamObserver<BatchUpdateBlobsResponse> responseObserver) {
        System.out.println(String.format("BL: I got batchUpdateBlobs request %s", request));
    }

    @Override
    public void batchReadBlobs(BatchReadBlobsRequest request, StreamObserver<BatchReadBlobsResponse> responseObserver) {
        System.out.println(String.format("BL: I got batchReadBlobs request %s", request));
    }

    @Override
    public void getTree(GetTreeRequest request, StreamObserver<GetTreeResponse> responseObserver) {
        System.out.println(String.format("BL: I got getTree request %s", request));
    }
}
