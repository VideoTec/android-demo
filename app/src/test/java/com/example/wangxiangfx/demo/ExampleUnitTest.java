package com.example.wangxiangfx.demo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        Integer empty;
        Integer a1 = Integer.valueOf(1);
        Integer a1_another = Integer.valueOf(1);
        Integer a500 = Integer.valueOf(500);
        Integer a500_another = Integer.valueOf(500);
        if (a1 == a1_another) {
            System.out.print("");
        }
        if (a500 != a500_another) {
            System.out.print("");
        }
    }
}