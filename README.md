# Remotecache

A simple, probably incomplete implementation of a remote cache.

## Quick Start

### Prerequisites
- [Bazel](https://bazel.build/install).

```shell
# Start the server
bazel run //src/main/java/me/blorente/remotecache:RemoteCache -- -p 50051
# Run the tests against it
bazel test --remote_cache=grpc://localhost:50051 //... 
# Build the entire project with it
bazel build --remote_cache=grpc://localhost:50051 //:RemoteCache 
```

In general, adding the `--remote_cache=grpc://localhost:<port>` to your Bazel build should be enough to use the remote cache.

## Contributing

### Build deployable binary

```shell
./tools/build-deployable.sh
```
We build a deployable jar that bundles a classpath, so it only needs java to run.

### Run the unit tests

```shell
bazel test //...
```

I have only added tests for the `CacheStorage` implementation. The rest of the codebase is mostly gRPC boilerplate,
so I've opted to just check it manually with Bazel by using [tools_remote](https://github.com/bazelbuild/tools_remote)
to inspect the gRPC log.

If I had more time, I'd have written automated integration tests using this tool (e.g. perform a blob write and validate that the blob is there).
However, I ran into issues integrating it in the Bazel build (it relies on an old version of remote-apis),
so I gave up on that for now.

### Managing third party dependencies

We use `rules_jvm_external` to manage Java dependencies.
To add a new dependency:
1. Add the desired Maven coordinate to `maven.install` in `MODULE.bazel`.
2. Run `tools/sync-deps.sh`.

In general, you'll have to run `./tools/sync-deps.sh` every time you change Java dependencies.

### Formatting the codebase

```shell
./tools/format.sh
```
We use Gazelle to generate BUILD files and Buildifier to format them.
I've been using IntelliJ's `google-java-format` plugin to format the java,
but the Bazel integration is lacking so I didn't include it in the project.

## Design Decisions

This project was developed on an M1 MacBook. It's likely compatible with Linux, but likely incompatible with Windows.

- `RemoteCache`: Main class and entry point for the implementation.
  - We don't handle authentication. We assume it's outside of the scope of the exercise.
    If I had to handle it, I'd generate signed JWTs and pass them as bearer tokens in the gRPC headers.
- `CapabilitiesImpl`: Implementation of the `Capabilities` gRPC service.
  - We don't accept any compression, for ease of implementation.
  - We only accept SHA256 as a digest function, again for ease of implementation.
- `ByteStreamImpl`: Implementation of the `ByteStream` gRPC service.
  - `queryWriteStatus()`:
    - We intentionally don't support resuming uploads, so we don't set `committedSize` on responses. If a write fails, it must be re-started.
  - `write()`:
    - We concatenate directly onto a `ByteString` every time we read a partial blob. There's probably a buffered implementation, but I haven't bothered for this exercise.
- `CASImpl`: Implementation of the `ContentAddressableStorage` gRPC service.
  - `getTree()`: Left unimplemented, didn't seem to affect the ability to operate.
  - `findMissingBlobs()`: FindMissingBlobs operations are batched, we don't stream responses. We should.
- `ACImpl`: Implementation of the `ActionCache` gRPC service.
  - On action write and reads, we should update the lifetime of the blobs that were accessed. I have run out of time to implement that.
  - `updateActionResult()`:
    - Should return proper error codes based on the error conditions in the spec. I've run out of time to implement them.
- `CacheStorage`: Storage backend.
  - We store all blobs in memory. In practice build caches should be optimized for `findMissingBlobs` latency, and this yields the fastest lookups.
  - I've chosen a very simple LRU strategy based on a fixed capacity of number of entries. This is suboptimal because blobs differ wildly in size (from single-digit bytest to several GBs)
    The next step would be to make the LRU less granular and instead store blocks of a certain fixed size in the cache,
    so that we can predict how many blocks will fit in the cache.
