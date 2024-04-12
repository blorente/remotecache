# Remotecache

## TODO
- Tests
- Cache eviction
- AC cache hits should ensure that the CAS items are fresh: https://github.com/bazelbuild/remote-apis/blob/1f36c310b28d762b258ea577ed08e8203274efae/build/bazel/remote/execution/v2/remote_execution.proto#L160
- AC update error returns: https://github.com/bazelbuild/remote-apis/blob/1f36c310b28d762b258ea577ed08e8203274efae/build/bazel/remote/execution/v2/remote_execution.proto#L188
- CAS lifetime increases on findmissingblobs
- Ensure the commit to remoteapis is upstream and I can access it, or fork and push

## Design Decisions

- This project was developed on an M1 MacBook. It's likely compatible with Linux, but likely incompatible with Windows.

- We don't handle authentication. We assume it's outside of the scope of the exercise. 
  If I had to handle it, I'd generate signed JWTs and pass them as bearer tokens in the gRPC headers.
- FindMissingBlobs operations are batched, we don't stream responses. We should.
- We expect at most one client at a time. We do not handle concurrent connections correctly.

- `CacheStorage`:
  - We store all blobs in memory. In practice build caches should be optimized for `findMissingBlobs` latency, and this yields the fastest lookups.
- `ByteStreamImpl`:
  - `queryWriteStatus()`:
    - We intentionally don't support resuming uploads, so we don't set `committedSize` on responses. If a write fails, it must be re-started.
  - `write()`:
    - We concatenate directly onto a `ByteString` every time we read a partial blob. There's probably a buffered implementation, but I haven't bothered for this exercise.
- `CASImpl`:
  - `getTree()`: Left unimplemented, didn't seem to affect the ability to operate.
- `CapabilitiesImpl`:
  - We don't accept any compression, for ease of implementation.
  - We only accept SHA256 as a digest function, again for ease of implementation.