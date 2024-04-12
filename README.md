# Remotecache

## TODO
- Tests
- Cache eviction
- AC cache hits should ensure that the CAS items are fresh: https://github.com/bazelbuild/remote-apis/blob/1f36c310b28d762b258ea577ed08e8203274efae/build/bazel/remote/execution/v2/remote_execution.proto#L160
- AC update error returns: https://github.com/bazelbuild/remote-apis/blob/1f36c310b28d762b258ea577ed08e8203274efae/build/bazel/remote/execution/v2/remote_execution.proto#L188
- Handle other digest functions?
- CAS lifetime increases on findmissingblobs
- Ensure the commit to remoteapis is upstream and I can access it, or fork and push

## Design Decisions

- We don't handle authentication. We assume it's outside of the scope of the exercise. 
  If I had to handle it, I'd generate signed JWTs and pass them as bearer tokens in the gRPC headers.
- FindMissingBlobs operations are batched, we don't stream responses. We should.
- We do not support compressed data.
- We expect at most one client at a time. We do not handle concurrent connections correctly.