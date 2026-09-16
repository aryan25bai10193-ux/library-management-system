# Library Management System (Java + JDBC/SQLite)

It's a librarian-side tool: manage a book catalog, register members, issue/return/renew loans, calculate overdue fines automatically, and export reports. Everything runs in the terminal and is backed by a real SQLite database through JDBC.

## What it does

- Add, remove, list, and search books by keyword
- Register members under STUDENT / FACULTY / GUEST tiers — each tier has its own borrowing limit and loan period
- Issue, return, and renew loans, with checks for availability, borrowing limits, and renewal eligibility (all enforced through custom exceptions)
- Calculate overdue fines automatically — Rs. 5/day after a 1-day grace period
- Run a background thread that scans for overdue loans every so often and logs them, without blocking the main CLI
- Export the catalog or the current overdue list to a timestamped `.txt` file, plus keep a running `activity.log`
- Store everything in SQLite, so there's no separate database server to install

## Built with

- Java 17+
- JDBC + the SQLite JDBC driver (`org.xerial:sqlite-jdbc`)
- Java Collections (`ArrayList`, `HashMap`)
- Java I/O streams (`BufferedWriter`/`FileWriter`) for reports and logging
- Maven for the build
- JUnit 5 for tests

## Project layout

```
library-management-system/
├── pom.xml
├── src/
│   ├── main/java/com/vit/library/
│   │   ├── model/        # Person, Member, Librarian, Book, Transaction, enums, interfaces
│   │   ├── exception/    # Custom checked/unchecked exceptions
│   │   ├── dao/          # JDBC data-access classes + DatabaseManager
│   │   ├── service/      # LibraryService - core business logic
│   │   ├── thread/       # OverdueChecker background thread
│   │   ├── util/         # FineCalculator, IsbnValidator, ReportExporter
│   │   └── ui/           # Main - CLI entry point
│   └── test/java/com/vit/library/service/   # JUnit 5 unit tests
├── docs/diagrams/        # Architecture, workflow, UML, and ER diagrams
├── data/                 # SQLite database file (created automatically)
└── reports/              # Exported reports and activity log (created automatically)
```

## Before you start

You'll need:

**JDK 17 or later.** Check with `java -version` and `javac -version`. Grab it from [Adoptium](https://adoptium.net/) if you don't have it, or install through your package manager (`sudo apt install openjdk-21-jdk` on Ubuntu).

**Maven 3.8+.** Check with `mvn -version`. Get it from [maven.apache.org](https://maven.apache.org/) or via your package manager (`sudo apt install maven`).

You don't need to install a database separately — SQLite is embedded, and Maven pulls the driver in for you.

## Setup

Clone it:

```bash
git clone https://github.com/{your-username}/{your-repo-name}.git
cd {your-repo-name}
```

Build it:

```bash
mvn clean package
```

This compiles everything, runs the unit tests, and produces a runnable jar at `target/library-management-system.jar` (the SQLite driver is bundled in, so there's no classpath juggling).

Want to skip the tests?

```bash
mvn clean package -DskipTests
```

## Running it

```bash
java -jar target/library-management-system.jar
```

You'll land on a menu:

```
=================================================
 LIBRARY MANAGEMENT SYSTEM
=================================================

---------------- MAIN MENU ----------------
 1. Add Book
 2. View All Books
 3. Search Books
 4. Add Member
 5. View All Members
 6. Search Members
 7. Issue Book
 8. Return Book
 9. Renew Loan
10. View Open Loans
11. View Overdue Loans
12. Export Catalog Report (to file)
13. Export Overdue Report (to file)
 0. Exit
Enter your choice:
```

Type a number, follow the prompts. On the first run it creates `data/library.db` and the tables it needs on its own — nothing to set up by hand.

A decent first run: add a book (1), register a member (4), then issue that book to the member (7) — that walks through the main workflow end to end.

## Changing the config

There's no separate config file. To change the fine rate or grace period, edit the constants in `src/main/java/com/vit/library/util/FineCalculator.java`. The overdue-checker's scan interval (30 seconds by default) is set where `OverdueChecker` gets constructed in `src/main/java/com/vit/library/ui/Main.java`.

## Testing

Unit tests cover fine calculation, ISBN validation, and loan renewal rules:

```bash
mvn test
```

If you'd rather test it by hand, run through the "first run" steps above and check that:

- View All Books (2) shows the book as `Issued`
- View Open Loans (10) shows the loan
- Return Book (8) marks it returned and reports any fine
- Export Catalog Report (12) writes a file under `reports/`

## Notes

- `data/library.db` and everything under `reports/` are generated at runtime and left out of version control (see `.gitignore`).
- It's terminal-only — no GUI setup needed to run or evaluate it.
