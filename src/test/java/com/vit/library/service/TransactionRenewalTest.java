package com.vit.library.service;

import com.vit.library.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRenewalTest {

    @Test
    void openLoanNotYetDueIsRenewable() {
        Transaction t = new Transaction(1, "9780132350884", "M001",
                LocalDate.now(), LocalDate.now().plusDays(5));
        assertTrue(t.isRenewable());
        LocalDate newDue = t.renew();
        assertEquals(LocalDate.now().plusDays(19), newDue);
        assertFalse(t.isRenewable(), "a loan should only be renewable once");
    }

    @Test
    void overdueLoanIsNotRenewable() {
        Transaction t = new Transaction(2, "9780132350884", "M002",
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(5));
        assertFalse(t.isRenewable());
        assertThrows(IllegalStateException.class, t::renew);
    }
}
