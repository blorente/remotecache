# Remotecache

## TODO
- Tests
- AC update error returns: https://github.com/bazelbuild/remote-apis/blob/1f36c310b28d762b258ea577ed08e8203274efae/build/bazel/remote/execution/v2/remote_execution.proto#L188
- CAS lifetime increases on findmissingblobs
- Ensure the commit to remoteapis is upstream and I can access it, or fork and push
- Write comments for top level modules

## Usage

## Testing

## Contributing

## Design Decisions

- This project was developed on an M1 MacBook. It's likely compatible with Linux, but likely incompatible with Windows.

- We don't handle authentication. We assume it's outside of the scope of the exercise. 
  If I had to handle it, I'd generate signed JWTs and pass them as bearer tokens in the gRPC headers.
- FindMissingBlobs operations are batched, we don't stream responses. We should.
- We expect at most one client at a time. We do not handle concurrent connections correctly.

- `CacheStorage`:
  - We store all blobs in memory. In practice build caches should be optimized for `findMissingBlobs` latency, and this yields the fastest lookups.
- `ByteStreamImpl`: Implementation of the `ByteStream` gRPC service.
  - `queryWriteStatus()`:
    - We intentionally don't support resuming uploads, so we don't set `committedSize` on responses. If a write fails, it must be re-started.
  - `write()`:
    - We concatenate directly onto a `ByteString` every time we read a partial blob. There's probably a buffered implementation, but I haven't bothered for this exercise.
- `CASImpl`: Implementation of the `ContentAddressableStorage` gRPC service.
  - `getTree()`: Left unimplemented, didn't seem to affect the ability to operate.
- `ACImpl`: Implementation of the `ActionCache` gRPC service.
  - On action write and reads, we should update the lifetime of the blobs that were accessed. I have run out of time to implement that.
  - `updateActionResult()`:
    - Should return proper error codes based on the error conditions in the spec. I've run out of time to implement them.
- `CapabilitiesImpl`:
  - We don't accept any compression, for ease of implementation.
  - We only accept SHA256 as a digest function, again for ease of implementation.