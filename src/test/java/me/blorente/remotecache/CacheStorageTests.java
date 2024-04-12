package me.blorente.remotecache;

import build.bazel.remote.execution.v2.Digest;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CacheStorageTests {

  private record TestBlob(Digest digest, ByteString data) {}

  private TestBlob createTestBlob() {
    // The digest doesn't match to the data, but we won't worry about that now.
    return new TestBlob(
        Digest.newBuilder()
            .setHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            .setSizeBytes(10)
            .build(),
        ByteString.copyFromUtf8("This is irrelevant"));
  }

  @Test
  public void testBlobStoredOnWrite() {
    CacheStorage cache = new CacheStorage();
    TestBlob blob = createTestBlob();

    Assertions.assertFalse(cache.hasBlob(blob.digest()));
    Assertions.assertNull(cache.getBlob(blob.digest()));

    cache.writeBlob(blob.digest(), blob.data());

    Assertions.assertTrue(cache.hasBlob(blob.digest()));
    Assertions.assertEquals(cache.getBlob(blob.digest()), blob.data());
  }
}
