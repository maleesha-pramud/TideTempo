package com.wigerlabs.tidetempo.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

public class ComboItemTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetId() {
        ComboItem instance = new ComboItem(1, "TestItem");
        int expResult = 1;
        int result = instance.getId();
        assertEquals("getId should return the initialized id", expResult, result);
    }

    @Test
    public void testGetName() {
        ComboItem instance = new ComboItem(1, "TestItem");
        String expResult = "TestItem";
        String result = instance.getName();
        assertEquals("getName should return the initialized name", expResult, result);
    }

    @Test
    public void testToString() {
        ComboItem instance = new ComboItem(1, "TestItem");
        String expResult = "TestItem";
        String result = instance.toString();
        assertEquals("toString should return the name", expResult, result);
    }
}
