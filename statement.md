# Project Statement

## Problem Statement

A lot of college and community libraries are still running on paper registers or a bare spreadsheet, and that causes the same headaches everywhere: you can't tell at a glance which copies are out, members forget due dates, overdue fines get calculated differently depending on who's doing the math (or not calculated at all), and there's no real record of who borrowed what and when.

This project is a terminal-based Library Management System that fixes that for a librarian — one place to manage the catalog, member records, and the whole lending cycle, with fines worked out automatically and a background process that keeps overdue tracking current even when nobody's actively checking.

## Scope of the Project

It covers the core lending workflow you'd expect from a small-to-medium library.

**In scope:**

- Managing a catalog of books (add, remove, list, keyword search)
- Registering and managing members across three tiers, each with its own borrowing limit and loan period
- Issuing, returning, and renewing loans, with validation on availability, borrowing limits, and renewal eligibility
- Automatic overdue fine calculation on return
- A background thread flagging overdue loans on its own schedule, separate from the main interaction loop
- Exporting catalog and overdue reports as timestamped text files
- Persisting everything in a local SQLite database so state survives a restart

**Out of scope (left for later):**

- A GUI or web front end — the brief calls for a command-line, terminal-runnable project
- Multi-branch support or inter-library loans
- Paying fines online
- Reservation queues for books that are already out
- Member login / authentication (right now the CLI is single-operator, run by library staff)

## Target Users

**Librarians / library staff** are the main users — they're the ones running the CLI day to day: adding books, registering members, issuing and returning loans, tracking fines, pulling reports.

**Members** are involved indirectly. Their borrowing activity, fines, and loan history all get tracked by the system, even though they don't touch the CLI themselves in this version.

## High-Level Features

1. **Book Catalog Management** — CRUD on books: ISBN, title, author, genre, publication year, and a live status (Available / Issued / Reserved / Lost).
2. **Member Management** — register members as STUDENT, FACULTY, or GUEST, each with its own borrowing limit and loan period; keeps track of each member's currently issued books and outstanding fines.
3. **Lending Workflow** — issue, return, and renew loans through one service layer, with every rule backed by a custom exception (`BookNotAvailableException`, `MemberLimitExceededException`, `MemberNotFoundException`, `BookNotFoundException`, `InvalidISBNException`).
4. **Automatic Fine Calculation** — `FineCalculator` applies a per-day rate after a short grace period, the same way every time a book comes back late.
5. **Background Overdue Monitoring** — a daemon thread checks open loans on a fixed interval and writes overdue alerts to a persistent activity log — this is where real concurrent access to the service layer comes in.
6. **Reporting** — export the full catalog or the current overdue list to timestamped `.txt` files using Java I/O streams.
7. **Persistent Storage** — books, members, librarians, and transactions all live in a local SQLite database through JDBC, so nothing's lost between runs.
