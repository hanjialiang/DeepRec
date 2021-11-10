package com.yudong.tflitegrudemo;

import junit.framework.TestCase;

public class DataLoaderTest extends TestCase {

    public void testGetBatch() {
        int[][] test = new int[10][10];
        int[] a = test[2];
        a[3] = 166;
        assertEquals(test[2][3], a[3]);
        assertEquals(test[2][3], 166);
    }
}