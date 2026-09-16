package com.vit.library.util;

public final class FineCalculator {

    public static final double FINE_PER_DAY = 5.0;
    public static final int GRACE_PERIOD_DAYS = 1;

    private FineCalculator() {
    }

    public static double calculate(long daysOverdue) {
        if (daysOverdue <= GRACE_PERIOD_DAYS) {
            return 0.0;
        }
        long billableDays = daysOverdue - GRACE_PERIOD_DAYS;
        return billableDays * FINE_PER_DAY;
    }
}
