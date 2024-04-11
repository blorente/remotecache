package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionCacheGrpc;
import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.GetActionResultRequest;
import build.bazel.remote.execution.v2.UpdateActionResultRequest;
import io.grpc.stub.StreamObserver;

public class ACImpl extends ActionCacheGrpc.ActionCacheImplBase {
    @Override
    public void getActionResult(GetActionResultRequest request, StreamObserver<ActionResult> responseObserver) {
        super.getActionResult(request, responseObserver);
    }

    @Override
    public void updateActionResult(UpdateActionResultRequest request, StreamObserver<ActionResult> responseObserver) {
        super.updateActionResult(request, responseObserver);
    }
}
