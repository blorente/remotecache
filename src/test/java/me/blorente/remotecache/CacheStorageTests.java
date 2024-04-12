package me.blorente.remotecache;

import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Digest;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CacheStorageTests {

  private record TestBlob(Digest digest, ByteString data) {}

  private Digest createUniqueTestDigest() {
    String uuid = UUID.randomUUID().toString();
    String hash = Hashing.sha256().hashString(uuid, StandardCharsets.UTF_8).toString();
    // The digest won't match to the data or the length, but we won't worry about that now.
    return Digest.newBuilder().setHash(hash).setSizeBytes(1).build();
  }

  private TestBlob createUniqueTestBlob() {
    return new TestBlob(createUniqueTestDigest(), ByteString.copyFromUtf8("This is irrelevant"));
  }

  private record TestAction(Digest digest, ActionResult data) {}

  private TestAction createUniqueTestAction() {
    // The digest doesn't match to the data, but we won't worry about that now.
    return new TestAction(createUniqueTestDigest(), ActionResult.getDefaultInstance());
  }

  @Test
  public void testBlobStoredOnWrite() {
    CacheStorage cache = new CacheStorage(10, 10);
    TestBlob blob = createUniqueTestBlob();

    Assertions.assertFalse(cache.hasBlob(blob.digest()));
    Assertions.assertNull(cache.getBlob(blob.digest()));

    cache.writeBlob(blob.digest(), blob.data());

    Assertions.assertTrue(cache.hasBlob(blob.digest()));
    Assertions.assertEquals(cache.getBlob(blob.digest()), blob.data());
  }

  @Test
  public void testActionStoredOnWrite() {
    CacheStorage cache = new CacheStorage(10, 10);
    TestAction action = createUniqueTestAction();

    Assertions.assertFalse(cache.hasAction(action.digest()));
    Assertions.assertNull(cache.getAction(action.digest()));

    cache.writeAction(action.digest(), action.data());

    Assertions.assertTrue(cache.hasAction(action.digest()));
    Assertions.assertEquals(cache.getAction(action.digest()), action.data());

    cache.writeAction(action.digest(), action.data());
  }

  @Test
  public void testBlobsGetEvicted() {
    CacheStorage cache = new CacheStorage(1, 0);
    TestBlob blob1 = createUniqueTestBlob();
    TestBlob blob2 = createUniqueTestBlob();

    cache.writeBlob(blob1.digest(), blob1.data());
    cache.writeBlob(blob2.digest(), blob2.data());

    Assertions.assertFalse(cache.hasBlob(blob1.digest()));
    Assertions.assertTrue(cache.hasBlob(blob2.digest()));
    Assertions.assertEquals(cache.getBlob(blob2.digest()), blob2.data());
  }

  @Test
  public void testGettingBlobUpdatesLifetime() {
    CacheStorage cache = new CacheStorage(2, 0);
    TestBlob blob1 = createUniqueTestBlob();
    TestBlob blob2 = createUniqueTestBlob();
    TestBlob blob3 = createUniqueTestBlob();

    // Fill the cache
    cache.writeBlob(blob1.digest(), blob1.data());
    cache.writeBlob(blob2.digest(), blob2.data());

    // Now the cache is full. Read blob 1 to refresh its lifetime
    cache.getBlob(blob1.digest());

    // Write blob3, expect blob2 to be evicted
    cache.writeBlob(blob3.digest(), blob3.data());

    Assertions.assertFalse(cache.hasBlob(blob2.digest()));
    Assertions.assertTrue(cache.hasBlob(blob1.digest()));
    Assertions.assertEquals(cache.getBlob(blob1.digest()), blob1.data());
    Assertions.assertTrue(cache.hasBlob(blob3.digest()));
    Assertions.assertEquals(cache.getBlob(blob3.digest()), blob3.data());
  }

  @Test
  public void testCheckingBlobExcistenceUpdatesLifetime() {
    CacheStorage cache = new CacheStorage(2, 0);
    TestBlob blob1 = createUniqueTestBlob();
    TestBlob blob2 = createUniqueTestBlob();
    TestBlob blob3 = createUniqueTestBlob();

    // Fill the cache
    cache.writeBlob(blob1.digest(), blob1.data());
    cache.writeBlob(blob2.digest(), blob2.data());

    // Now the cache is full. Read blob 1 to refresh its lifetime
    cache.hasBlob(blob1.digest());

    // Write blob3, expect blob2 to be evicted
    cache.writeBlob(blob3.digest(), blob3.data());

    Assertions.assertFalse(cache.hasBlob(blob2.digest()));
    Assertions.assertTrue(cache.hasBlob(blob1.digest()));
    Assertions.assertEquals(cache.getBlob(blob1.digest()), blob1.data());
    Assertions.assertTrue(cache.hasBlob(blob3.digest()));
    Assertions.assertEquals(cache.getBlob(blob3.digest()), blob3.data());
  }

  @Test
  public void testGettingActionUpdatesLifetime() {
    CacheStorage cache = new CacheStorage(0, 2);
    TestAction action1 = createUniqueTestAction();
    TestAction action2 = createUniqueTestAction();
    TestAction action3 = createUniqueTestAction();

    // Fill the cache
    cache.writeAction(action1.digest(), action1.data());
    cache.writeAction(action2.digest(), action2.data());

    // Now the cache is full. Read action1 to refresh its lifetime
    cache.getAction(action1.digest());

    // Write action3, expect action2 to be evicted
    cache.writeAction(action3.digest(), action3.data());

    Assertions.assertFalse(cache.hasAction(action2.digest()));
    Assertions.assertTrue(cache.hasAction(action1.digest()));
    Assertions.assertEquals(cache.getAction(action1.digest()), action1.data());
    Assertions.assertTrue(cache.hasAction(action3.digest()));
    Assertions.assertEquals(cache.getAction(action3.digest()), action3.data());
  }

  @Test
  public void testCheckingActionExcistenceUpdatesLifetime() {
    CacheStorage cache = new CacheStorage(0, 2);
    TestAction action1 = createUniqueTestAction();
    TestAction action2 = createUniqueTestAction();
    TestAction action3 = createUniqueTestAction();

    // Fill the cache
    cache.writeAction(action1.digest(), action1.data());
    cache.writeAction(action2.digest(), action2.data());

    // Now the cache is full. Read action1 to refresh its lifetime
    cache.hasAction(action1.digest());

    // Write action3, expect action2 to be evicted
    cache.writeAction(action3.digest(), action3.data());

    Assertions.assertFalse(cache.hasAction(action2.digest()));
    Assertions.assertTrue(cache.hasAction(action1.digest()));
    Assertions.assertEquals(cache.getAction(action1.digest()), action1.data());
    Assertions.assertTrue(cache.hasAction(action3.digest()));
    Assertions.assertEquals(cache.getAction(action3.digest()), action3.data());
  }
}
