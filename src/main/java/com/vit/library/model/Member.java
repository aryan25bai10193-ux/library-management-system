package com.vit.library.model;

import java.time.LocalDate;

public class Member extends Person {

    private MembershipType membershipType;
    private final LocalDate joinDate;
    private int currentBooksIssued;
    private double outstandingFine;

    public Member(String id, String name, String email, String phone,
                   MembershipType membershipType, LocalDate joinDate) {
        super(id, name, email, phone);
        this.membershipType = membershipType;
        this.joinDate = joinDate;
        this.currentBooksIssued = 0;
        this.outstandingFine = 0.0;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    @Override
    public String describe() {
        return super.describe() + String.format(" | %s | Books issued: %d/%d | Fine due: Rs.%.2f",
                membershipType, currentBooksIssued, membershipType.getMaxBooksAllowed(), outstandingFine);
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public int getCurrentBooksIssued() {
        return currentBooksIssued;
    }

    public void incrementBooksIssued() {
        this.currentBooksIssued++;
    }

    public void decrementBooksIssued() {
        if (this.currentBooksIssued > 0) {
            this.currentBooksIssued--;
        }
    }

    public boolean canBorrowMore() {
        return currentBooksIssued < membershipType.getMaxBooksAllowed();
    }

    public double getOutstandingFine() {
        return outstandingFine;
    }

    public void addFine(double amount) {
        if (amount > 0) {
            this.outstandingFine += amount;
        }
    }

    public void payFine(double amount) {
        this.outstandingFine = Math.max(0.0, this.outstandingFine - amount);
    }
}
