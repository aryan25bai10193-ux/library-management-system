package com.vit.library.model;

public enum MembershipType {
    STUDENT(3, 14),
    FACULTY(6, 30),
    GUEST(1, 7);

    private final int maxBooksAllowed;
    private final int loanPeriodDays;

    MembershipType(int maxBooksAllowed, int loanPeriodDays) {
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanPeriodDays = loanPeriodDays;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    @Override
    public String toString() {
        return name() + " (max " + maxBooksAllowed + " books, " + loanPeriodDays + "-day loan)";
    }
}
