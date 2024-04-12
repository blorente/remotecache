package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Digest;
import com.google.protobuf.ByteString;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/***
 * CacheStorage holds the raw storage of blobs and actions.
 * It is responsible for lookups, updating lifetimes, and evictions.
 */
public class CacheStorage {

    private final LRUCache<ByteString> cas;
    private final LRUCache<ActionResult> ac;

    public CacheStorage(int casCapacity, int acCapacity) {
        this.cas = new LRUCache<>(casCapacity);
        this.ac = new LRUCache<>(acCapacity);
    }

    public final void writeBlob(Digest digest, ByteString data) {
        this.cas.insert(digest, data);
    }

    public final boolean hasBlob(Digest digest) {
        return this.cas.has(digest);
    }

    public final ByteString getBlob(Digest digest) {
        return this.cas.get(digest);
    }

    public final void writeAction(Digest digest, ActionResult data) {
        this.ac.insert(digest, data);
    }

    public final boolean hasAction(Digest digest) {
        return this.ac.has(digest);
    }

    public final ActionResult getAction(Digest digest) {
        return this.ac.get(digest);
    }
}
