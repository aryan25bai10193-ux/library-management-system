package com.vit.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Transaction implements Renewable {

    private final int transactionId;
    private final String isbn;
    private final String memberId;
    private final LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean renewed;
    private double fineCharged;

    public Transaction(int transactionId, String isbn, String memberId,
                        LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.renewed = false;
        this.fineCharged = 0.0;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOpen() {
        return returnDate == null;
    }

    public long daysOverdue(LocalDate asOf) {
        LocalDate reference = (returnDate != null) ? returnDate : asOf;
        long diff = ChronoUnit.DAYS.between(dueDate, reference);
        return Math.max(0, diff);
    }

    public double getFineCharged() {
        return fineCharged;
    }

    public void setFineCharged(double fineCharged) {
        this.fineCharged = fineCharged;
    }

    @Override
    public LocalDate renew() {
        if (!isRenewable()) {
            throw new IllegalStateException("This loan is not eligible for renewal");
        }
        this.dueDate = this.dueDate.plusDays(14);
        this.renewed = true;
        return this.dueDate;
    }

    @Override
    public boolean isRenewable() {
        return isOpen() && !renewed && daysOverdue(LocalDate.now()) == 0;
    }

    @Override
    public String toString() {
        String status = isOpen() ? "OPEN" : "RETURNED on " + returnDate;
        return String.format("Txn#%d | ISBN:%s | Member:%s | Issued:%s | Due:%s | %s",
                transactionId, isbn, memberId, issueDate, dueDate, status);
    }
}
