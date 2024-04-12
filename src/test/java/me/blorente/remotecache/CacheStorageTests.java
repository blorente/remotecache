package me.blorente.remotecache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheStorageTests {

    String message = "Robert";

    @Test
    public void testPrintMessage() {
        System.out.println("Inside testPrintMessage()");
        assertEquals(message, "Roberty");
    }
}
