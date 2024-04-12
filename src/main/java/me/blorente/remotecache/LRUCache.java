package me.blorente.remotecache;

import build.bazel.remote.execution.v2.Digest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/***
 * LRUCache implements a simple LRU cache.
 * @param <Val> The value stored.
 */
public class LRUCache<Val> {
  private record LRUCacheEntry<V>(Digest digest, V value) {}

  class Node {
    LRUCacheEntry<Val> data;
    Node prev;
    Node next;

    public Node(LRUCacheEntry<Val> data) {
      this.data = data;
      this.prev = null;
      this.next = null;
    }
  }

 private int capacity;
  private final ConcurrentMap<Digest, Node> nodes = new ConcurrentHashMap<>();
 private Node head;
 private Node tail;

  public LRUCache(int capacity) {
    this.capacity = capacity;
  }

  private void removeNode(Node node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
    if (tail == node) {
        tail = node.prev;
    }
    if (head == node) {
        head = node.next;
    }
  }

  private void addToFront(Node node) {
    if (head == null) {
      head = node;
      head.next = tail;
      tail = node;
      tail.prev = head;
    }

    node.next = head;
    head.prev = node;
    node.prev = tail;
    tail.next = node;
    head = node;
  }

  private void updateLifetime(Digest digest) {
    Node node = nodes.get(digest);
    removeNode(node);
    addToFront(node);
  }

  private void evictOne() {
      nodes.remove(tail.data.digest);
      removeNode(tail);
  }

  protected Val get(Digest digest) {
    if (!nodes.containsKey(digest)) {
      return null;
    }
    Node node = nodes.get(digest);
    updateLifetime(digest);
    return node.data.value();
  }

  protected boolean has(Digest digest) {
    if (!nodes.containsKey(digest)) {
      return false;
    }
    updateLifetime(digest);
    return true;
  }

  protected void insert(Digest digest, Val value) {
    LRUCacheEntry<Val> entry = new LRUCacheEntry<>(digest, value);
    if (has(digest)) {
      Node node = nodes.get(digest);
      node.data = entry;
      updateLifetime(digest);
    } else {
      Node node = new Node(entry);
      nodes.put(digest, node);
      if (nodes.size() > capacity) {
        evictOne();
      }
      addToFront(node);
    }
  }
}
