package com.vit.library.service;

import com.vit.library.dao.BookDAO;
import com.vit.library.dao.MemberDAO;
import com.vit.library.dao.TransactionDAO;
import com.vit.library.exception.BookNotAvailableException;
import com.vit.library.exception.BookNotFoundException;
import com.vit.library.exception.MemberLimitExceededException;
import com.vit.library.exception.MemberNotFoundException;
import com.vit.library.model.Book;
import com.vit.library.model.BookStatus;
import com.vit.library.model.Member;
import com.vit.library.model.Transaction;
import com.vit.library.util.FineCalculator;
import com.vit.library.util.ReportExporter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private final Map<String, Book> catalogCache = new HashMap<>();

    public void addBook(Book book) throws SQLException {
        bookDAO.addBook(book);
        catalogCache.put(book.getIsbn(), book);
    }

    public boolean removeBook(String isbn) throws SQLException {
        boolean removed = bookDAO.deleteBook(isbn);
        catalogCache.remove(isbn);
        return removed;
    }

    public List<Book> listAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        return bookDAO.search(keyword);
    }

    public void addMember(Member member) throws SQLException {
        memberDAO.addMember(member);
    }

    public List<Member> listAllMembers() throws SQLException {
        return memberDAO.getAllMembers();
    }

    public List<Member> searchMembers(String keyword) {
        return memberDAO.search(keyword);
    }

    public Member getMemberWithLiveCount(String memberId) throws SQLException, MemberNotFoundException {
        Member member = memberDAO.getById(memberId);
        int openLoans = transactionDAO.getOpenTransactionsForMember(memberId).size();
        for (int i = 0; i < openLoans; i++) {
            member.incrementBooksIssued();
        }
        return member;
    }

    public synchronized Transaction issueBook(String isbn, String memberId)
            throws SQLException, BookNotFoundException, BookNotAvailableException,
            MemberNotFoundException, MemberLimitExceededException {

        Book book = bookDAO.getByIsbn(isbn);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(isbn);
        }

        Member member = getMemberWithLiveCount(memberId);
        if (!member.canBorrowMore()) {
            throw new MemberLimitExceededException(memberId, member.getMembershipType().getMaxBooksAllowed());
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(member.getMembershipType().getLoanPeriodDays());

        Transaction transaction = new Transaction(0, isbn, memberId, issueDate, dueDate);
        int generatedId = transactionDAO.createTransaction(transaction);

        bookDAO.updateStatus(isbn, BookStatus.ISSUED);
        catalogCache.remove(isbn);

        ReportExporter.appendToLog("ISSUED book " + isbn + " to member " + memberId + ", due " + dueDate);

        return new Transaction(generatedId, isbn, memberId, issueDate, dueDate);
    }

    public synchronized double returnBook(String isbn, String memberId)
            throws SQLException, BookNotFoundException, MemberNotFoundException {

        List<Transaction> open = transactionDAO.getOpenTransactionForBook(isbn);
        Transaction match = open.stream()
                .filter(t -> t.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(isbn));

        LocalDate returnDate = LocalDate.now();
        long overdueDays = match.daysOverdue(returnDate);
        double fine = FineCalculator.calculate(overdueDays);

        transactionDAO.markReturned(match.getTransactionId(), returnDate, fine);
        bookDAO.updateStatus(isbn, BookStatus.AVAILABLE);
        catalogCache.remove(isbn);

        if (fine > 0) {
            Member member = memberDAO.getById(memberId);
            member.addFine(fine);
            memberDAO.updateFine(memberId, member.getOutstandingFine());
        }

        ReportExporter.appendToLog("RETURNED book " + isbn + " from member " + memberId
                + (fine > 0 ? (", fine charged: Rs." + fine) : ", on time"));

        return fine;
    }

    public synchronized LocalDate renewLoan(String isbn, String memberId)
            throws SQLException, BookNotFoundException {
        List<Transaction> open = transactionDAO.getOpenTransactionForBook(isbn);
        Transaction match = open.stream()
                .filter(t -> t.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(isbn));

        LocalDate newDueDate = match.renew();
        transactionDAO.updateDueDateAndRenewed(match.getTransactionId(), newDueDate);
        ReportExporter.appendToLog("RENEWED book " + isbn + " for member " + memberId + ", new due " + newDueDate);
        return newDueDate;
    }

    public List<Transaction> getAllOpenLoans() throws SQLException {
        return transactionDAO.getAllOpenTransactions();
    }

    public List<Transaction> getOverdueLoans() throws SQLException {
        LocalDate today = LocalDate.now();
        return getAllOpenLoans().stream()
                .filter(t -> t.daysOverdue(today) > 0)
                .toList();
    }

    public String exportCatalogReport() throws SQLException, IOException {
        return ReportExporter.exportCatalog(listAllBooks());
    }

    public String exportOverdueReport() throws SQLException, IOException {
        return ReportExporter.exportOverdueReport(getOverdueLoans());
    }
}
