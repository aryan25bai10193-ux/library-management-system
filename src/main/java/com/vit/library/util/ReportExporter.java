package com.vit.library.util;

import com.vit.library.model.Book;
import com.vit.library.model.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ReportExporter {

    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ReportExporter() {
    }

    public static String exportCatalog(List<Book> books) throws IOException {
        new File(REPORTS_DIR).mkdirs();
        String filename = REPORTS_DIR + File.separator + "catalog_" + LocalDateTime.now().format(TS) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("LIBRARY CATALOG REPORT");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now());
            writer.newLine();
            writer.write("=".repeat(100));
            writer.newLine();
            for (Book b : books) {
                writer.write(b.toString());
                writer.newLine();
            }
            writer.write("=".repeat(100));
            writer.newLine();
            writer.write("Total books: " + books.size());
            writer.newLine();
        }
        return filename;
    }

    public static String exportOverdueReport(List<Transaction> overdue) throws IOException {
        new File(REPORTS_DIR).mkdirs();
        String filename = REPORTS_DIR + File.separator + "overdue_" + LocalDateTime.now().format(TS) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("OVERDUE BOOKS REPORT");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now());
            writer.newLine();
            writer.write("=".repeat(100));
            writer.newLine();
            for (Transaction t : overdue) {
                writer.write(t.toString());
                writer.newLine();
            }
            writer.write("=".repeat(100));
            writer.newLine();
            writer.write("Total overdue loans: " + overdue.size());
            writer.newLine();
        }
        return filename;
    }

    public static void appendToLog(String line) {
        new File(REPORTS_DIR).mkdirs();
        String filename = REPORTS_DIR + File.separator + "activity.log";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write("[" + LocalDateTime.now() + "] " + line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Warning: could not write to activity log: " + e.getMessage());
        }
    }
}
