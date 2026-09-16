package com.vit.library.ui;

import com.vit.library.dao.DatabaseManager;
import com.vit.library.exception.BookNotAvailableException;
import com.vit.library.exception.BookNotFoundException;
import com.vit.library.exception.InvalidISBNException;
import com.vit.library.exception.MemberLimitExceededException;
import com.vit.library.exception.MemberNotFoundException;
import com.vit.library.model.Book;
import com.vit.library.model.Member;
import com.vit.library.model.MembershipType;
import com.vit.library.model.Transaction;
import com.vit.library.service.LibraryService;
import com.vit.library.thread.OverdueChecker;
import com.vit.library.util.IsbnValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        try {
            DatabaseManager.initializeSchema();
        } catch (SQLException e) {
            System.err.println("Fatal: could not initialize the database: " + e.getMessage());
            return;
        }

        OverdueChecker overdueChecker = new OverdueChecker(service, 30_000);
        overdueChecker.start();

        System.out.println("=================================================");
        System.out.println(" LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=================================================");

        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addBook();
                    case "2" -> viewAllBooks();
                    case "3" -> searchBooks();
                    case "4" -> addMember();
                    case "5" -> viewAllMembers();
                    case "6" -> searchMembers();
                    case "7" -> issueBook();
                    case "8" -> returnBook();
                    case "9" -> renewLoan();
                    case "10" -> viewOpenLoans();
                    case "11" -> viewOverdueLoans();
                    case "12" -> exportCatalogReport();
                    case "13" -> exportOverdueReport();
                    case "0" -> exit = true;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InvalidISBNException e) {

                System.out.println("Input error: " + e.getMessage());
            } catch (BookNotFoundException | MemberNotFoundException
                     | BookNotAvailableException | MemberLimitExceededException e) {

                System.out.println("Operation failed: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File I/O error while writing report: " + e.getMessage());
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Could not complete request: " + e.getMessage());
            }
        }

        System.out.println("Shutting down background threads...");
        overdueChecker.stopChecking();
        DatabaseManager.close();
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println(" 1. Add Book");
        System.out.println(" 2. View All Books");
        System.out.println(" 3. Search Books");
        System.out.println(" 4. Add Member");
        System.out.println(" 5. View All Members");
        System.out.println(" 6. Search Members");
        System.out.println(" 7. Issue Book");
        System.out.println(" 8. Return Book");
        System.out.println(" 9. Renew Loan");
        System.out.println("10. View Open Loans");
        System.out.println("11. View Overdue Loans");
        System.out.println("12. Export Catalog Report (to file)");
        System.out.println("13. Export Overdue Report (to file)");
        System.out.println(" 0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addBook() throws SQLException {
        System.out.print("ISBN (10 or 13 digits): ");
        String isbn = IsbnValidator.validateAndNormalize(scanner.nextLine());
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Publication year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());

        service.addBook(new Book(isbn, title, author, genre, year));
        System.out.println("Book added successfully.");
    }

    private static void viewAllBooks() throws SQLException {
        List<Book> books = service.listAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the catalog yet.");
            return;
        }
        books.forEach(System.out::println);
    }

    private static void searchBooks() {
        System.out.print("Search keyword (title/author/genre/ISBN): ");
        String keyword = scanner.nextLine();
        List<Book> results = service.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void addMember() throws SQLException {
        System.out.print("Member ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Membership type (STUDENT / FACULTY / GUEST): ");
        MembershipType type = MembershipType.valueOf(scanner.nextLine().trim().toUpperCase());

        service.addMember(new Member(id, name, email, phone, type, LocalDate.now()));
        System.out.println("Member registered successfully.");
    }

    private static void viewAllMembers() throws SQLException {
        List<Member> members = service.listAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered yet.");
            return;
        }
        members.forEach(System.out::println);
    }

    private static void searchMembers() {
        System.out.print("Search keyword (name/ID/email): ");
        String keyword = scanner.nextLine();
        List<Member> results = service.searchMembers(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching members found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void issueBook() throws SQLException, BookNotFoundException,
            BookNotAvailableException, MemberNotFoundException, MemberLimitExceededException {
        System.out.print("ISBN: ");
        String isbn = IsbnValidator.validateAndNormalize(scanner.nextLine());
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();

        Transaction t = service.issueBook(isbn, memberId);
        System.out.println("Book issued. " + t);
    }

    private static void returnBook() throws SQLException, BookNotFoundException, MemberNotFoundException {
        System.out.print("ISBN: ");
        String isbn = IsbnValidator.validateAndNormalize(scanner.nextLine());
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();

        double fine = service.returnBook(isbn, memberId);
        if (fine > 0) {
            System.out.printf("Book returned. Overdue fine charged: Rs.%.2f%n", fine);
        } else {
            System.out.println("Book returned on time. No fine.");
        }
    }

    private static void renewLoan() throws SQLException, BookNotFoundException {
        System.out.print("ISBN: ");
        String isbn = IsbnValidator.validateAndNormalize(scanner.nextLine());
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();

        LocalDate newDueDate = service.renewLoan(isbn, memberId);
        System.out.println("Loan renewed. New due date: " + newDueDate);
    }

    private static void viewOpenLoans() throws SQLException {
        List<Transaction> loans = service.getAllOpenLoans();
        if (loans.isEmpty()) {
            System.out.println("No open loans.");
            return;
        }
        loans.forEach(System.out::println);
    }

    private static void viewOverdueLoans() throws SQLException {
        List<Transaction> overdue = service.getOverdueLoans();
        if (overdue.isEmpty()) {
            System.out.println("No overdue loans. Everything is on time.");
            return;
        }
        overdue.forEach(System.out::println);
    }

    private static void exportCatalogReport() throws SQLException, IOException {
        String path = service.exportCatalogReport();
        System.out.println("Catalog report written to: " + path);
    }

    private static void exportOverdueReport() throws SQLException, IOException {
        String path = service.exportOverdueReport();
        System.out.println("Overdue report written to: " + path);
    }
}
