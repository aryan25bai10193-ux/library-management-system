package com.vit.library.thread;

import com.vit.library.model.Transaction;
import com.vit.library.service.LibraryService;
import com.vit.library.util.FineCalculator;
import com.vit.library.util.ReportExporter;

import java.time.LocalDate;
import java.util.List;

public class OverdueChecker extends Thread {

    private final LibraryService libraryService;
    private final long intervalMillis;
    private volatile boolean running = true;

    public OverdueChecker(LibraryService libraryService, long intervalMillis) {
        super("OverdueChecker-Thread");
        this.libraryService = libraryService;
        this.intervalMillis = intervalMillis;
        setDaemon(true);
    }

    public void stopChecking() {
        this.running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                scanOnce();
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        ReportExporter.appendToLog("OverdueChecker thread stopped.");
    }

    private void scanOnce() {
        try {
            List<Transaction> overdue = libraryService.getOverdueLoans();
            LocalDate today = LocalDate.now();
            for (Transaction t : overdue) {
                long days = t.daysOverdue(today);
                double previewFine = FineCalculator.calculate(days);
                ReportExporter.appendToLog(String.format(
                        "OVERDUE CHECK: %s is %d day(s) overdue for member %s (est. fine so far: Rs.%.2f)",
                        t.getIsbn(), days, t.getMemberId(), previewFine));
            }
            if (!overdue.isEmpty()) {
                ReportExporter.appendToLog("OverdueChecker: " + overdue.size() + " loan(s) currently overdue.");
            }
        } catch (Exception e) {
            ReportExporter.appendToLog("OverdueChecker encountered an error: " + e.getMessage());
        }
    }
}
