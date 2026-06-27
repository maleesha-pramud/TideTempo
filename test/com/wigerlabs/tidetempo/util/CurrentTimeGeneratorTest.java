package com.wigerlabs.tidetempo.util;

import java.sql.Timestamp;
import org.junit.Test;
import static org.junit.Assert.*;

public class CurrentTimeGeneratorTest {

    @Test
    public void testGetInstance() {
        CurrentTimeGenerator instance1 = CurrentTimeGenerator.getInstance();
        CurrentTimeGenerator instance2 = CurrentTimeGenerator.getInstance();
        
        assertNotNull("Instance should not be null", instance1);
        assertSame("getInstance should return the exact same singleton instance", instance1, instance2);
    }

    @Test
    public void testGetCurrentTime() {
        CurrentTimeGenerator instance = CurrentTimeGenerator.getInstance();
        Timestamp result = instance.getCurrentTime();
        
        assertNotNull("getCurrentTime should return a valid Timestamp", result);
        
        // Ensure the timestamp generated is very close to current system time
        long timeDiff = Math.abs(System.currentTimeMillis() - result.getTime());
        assertTrue("Timestamp should be within 1000ms of current system time", timeDiff < 1000);
    }
}
