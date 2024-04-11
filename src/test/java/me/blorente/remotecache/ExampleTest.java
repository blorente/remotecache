package me.blorente.remotecache;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

public class ExampleTest {

    String message = "Robert";

    @Test
    public void testPrintMessage() {
        System.out.println("Inside testPrintMessage()");
        assertEquals(message, "Robert");
    }
}