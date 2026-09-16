package com.vit.library.service;

import com.vit.library.util.FineCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FineCalculatorTest {

    @Test
    void noFineWithinGracePeriod() {
        assertEquals(0.0, FineCalculator.calculate(0));
        assertEquals(0.0, FineCalculator.calculate(1));
    }

    @Test
    void fineAccruesAfterGracePeriod() {

        assertEquals(5.0, FineCalculator.calculate(2));
        assertEquals(25.0, FineCalculator.calculate(6));
    }

    @Test
    void fineNeverNegative() {
        assertEquals(0.0, FineCalculator.calculate(-3));
    }
}
