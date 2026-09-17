# Project Statement

## Problem Statement

Many college and community libraries still use paper registers or simple spreadsheets to manage their books and members. This often makes it difficult to know which copies are currently available, which books have been issued, when they are due, and whether members have outstanding fines. It also makes searching the catalog, tracking returns, and preparing reports more time-consuming.

This project is a terminal-based Library Management System designed for librarians. It provides one place to manage the catalog, member records, and the complete lending process. The system also calculates fines automatically and stores library data in a local database so that it remains available after the program is restarted.

## Scope of the Project

The project covers the main lending activities expected in a small-to-medium-sized library.

**In scope:**

- Managing a catalog of books, including adding, removing, listing, and searching by keyword
- Registering and managing members across three tiers, with each tier having its own borrowing limit and loan period
- Issuing, returning, and renewing loans
- Validating book availability, member borrowing limits, and renewal eligibility
- Automatically calculating overdue fines when books are returned
- Running a background thread that checks for overdue loans on its own schedule, separately from the main interaction loop
- Exporting catalog and overdue-loan reports as timestamped text files
- Storing all information in a local SQLite database so that the state is preserved between restarts

**Out of scope (left for later):**

- A GUI or web front end, since the project is intended to be run from the command line
- Support for multiple library branches or inter-library loans
- Online payment of fines
- Reservation queues for books that are already issued
- Member login or authentication, since the CLI is operated by library staff

## Target Users

**Librarians and library staff** are the main users of the system. They use the CLI to add books, register members, issue and return loans, renew books, track fines, and generate reports.

**Members** are involved indirectly. The system records their borrowing activity, fines, and loan history, even though members do not use the CLI directly in this version.

## High-Level Features

1. **Book Catalog Management** — The system supports basic CRUD operations for books. Book records include the ISBN, title, author, genre, publication year, and current status. A book can be marked as Available, Issued, Reserved, or Lost.

2. **Member Management** — Members can be registered as STUDENT, FACULTY, or GUEST. Each member type has its own borrowing limit and loan period. The system also keeps track of each member's currently issued books and outstanding fines.

3. **Lending Workflow** — Books can be issued, returned, and renewed through a service layer. The system checks each lending rule and reports invalid operations through custom exceptions such as `BookNotAvailableException` and `MemberLimitExceededException`, along with other loan-related exceptions.

4. **Automatic Fine Calculation** — `FineCalculator` applies a per-day fine after a short grace period. This ensures that overdue fines are calculated consistently whenever a book is returned late.

5. **Background Overdue Monitoring** — A daemon thread checks open loans at a fixed interval and writes overdue alerts to a persistent activity log. This feature also introduces concurrent access to the system while the main interaction loop is running.

6. **Reporting** — The system can export the complete catalog and the current list of overdue loans to timestamped `.txt` files using Java I/O streams.

7. **Persistent Storage** — Books, members, librarians, and transactions are stored in a local SQLite database through JDBC. This ensures that library records are not lost when the application is closed and started again.
