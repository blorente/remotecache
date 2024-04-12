package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Digest;
import com.google.protobuf.ByteString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheStorage {

    private final ConcurrentMap<Digest, ActionResult> ac = new ConcurrentHashMap<>();
    private final ConcurrentMap<Digest, ByteString> cas = new ConcurrentHashMap<>();

    public final ConcurrentMap<Digest, ActionResult> ac() {
        return this.ac;
    }

    public final ConcurrentMap<Digest, ByteString> cas() {
        return this.cas;
    }
}
