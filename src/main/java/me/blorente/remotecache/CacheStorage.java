package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Digest;
import com.google.protobuf.ByteString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheStorage {

    private final ConcurrentMap<Digest, ActionResult> ac = new ConcurrentHashMap<>();
    private final ConcurrentMap<Digest, ByteString> cas = new ConcurrentHashMap<>();

    public final void writeBlob(Digest digest, ByteString data) {
        // TODO BL: Update lifetime
        this.cas.put(digest, data);
    }

    public final boolean hasBlob(Digest digest) {
        // TODO BL: Update lifetime
        return this.cas.containsKey(digest);
    }

    public final ByteString getBlob(Digest digest) {
        // TODO BL: Update lifetime
        return this.cas.get(digest);
    }

    public final void writeAction(Digest digest, ActionResult data) {
        // TODO BL: Update lifetime
        this.ac.put(digest, data);
    }

    public final boolean hasAction(Digest digest) {
        // TODO BL: Update lifetime
        return this.ac.containsKey(digest);
    }

    public final ActionResult getAction(Digest digest) {
        // TODO BL: Update lifetime
        return this.ac.get(digest);
    }
}
