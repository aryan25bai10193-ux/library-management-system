# Library Management System

A command-line library management system built with Java, JDBC, and SQLite.

The application allows librarians to manage books and members, issue and return books, renew loans, calculate overdue fines, and export reports. All data is stored locally in SQLite, so no separate database server is required.

## Features

- Add, remove, list, and search books
- Register members under the `STUDENT`, `FACULTY`, or `GUEST` categories
- Apply different borrowing limits and loan periods for each member category
- Issue, return, and renew books
- Check book availability before issuing a loan
- Enforce borrowing limits and renewal rules
- Handle errors using custom exceptions
- Calculate overdue fines automatically
- Charge Rs. 5 per day after a one-day grace period
- Run a background thread that checks for overdue loans
- Export the book catalog and overdue loans to timestamped text files
- Maintain an `activity.log` file
- Store application data in an SQLite database

## Technologies Used

- Java 17 or later
- JDBC
- SQLite
- Maven
- JUnit 5
- Java Collections, including `ArrayList` and `HashMap`
- Java I/O classes such as `BufferedWriter` and `FileWriter`

## Project Structure

```text
library-management-system/
├── pom.xml
├── src/
│   ├── main/java/com/vit/library/
│   │   ├── model/        # Person, Member, Librarian, Book, Transaction, enums, and interfaces
│   │   ├── exception/    # Custom checked and unchecked exceptions
│   │   ├── dao/          # JDBC data-access classes and DatabaseManager
│   │   ├── service/      # LibraryService and application logic
│   │   ├── thread/       # Background overdue checker
│   │   ├── util/         # FineCalculator, IsbnValidator, and ReportExporter
│   │   └── ui/           # Main command-line entry point
│   └── test/java/com/vit/library/service/
├── docs/diagrams/        # Architecture, workflow, UML, and ER diagrams
├── data/                 # SQLite database created at runtime
└── reports/              # Exported reports and activity log
```

## Requirements

You need the following installed:

- JDK 17 or later
- Maven 3.8 or later

Check your Java installation:

```bash
java -version
javac -version
```

Check your Maven installation:

```bash
mvn -version
```

If Java is not installed, you can download it from [Adoptium](https://adoptium.net/).

Maven is available from [maven.apache.org](https://maven.apache.org/) and through most package managers.

You do not need to install SQLite separately. The database is embedded, and Maven downloads the SQLite JDBC driver during the build.

## Setup

Clone the repository:

```bash
git clone https://github.com/aryan25bai10193-ux/library-management-system.git
cd library-management-system
```

## Build

Build the project with:

```bash
mvn clean package
```

This compiles the source code, runs the unit tests, and creates the executable jar:

```text
target/library-management-system.jar
```

The SQLite driver is included in the jar, so no additional classpath configuration is needed.

To build without running the tests:

```bash
mvn clean package -DskipTests
```

## Running the Application

Start the application with:

```bash
java -jar target/library-management-system.jar
```

The main menu includes the following options:

```text
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

Enter a menu number and follow the prompts.

On the first run, the application creates the following automatically:

- `data/library.db`
- The required database tables
- The `reports/` directory when reports or logs are created

Nothing needs to be configured manually.

## Basic Workflow

To test the main workflow:

1. Add a book using option `1`.
2. Register a member using option `4`.
3. Issue the book using option `7`.
4. View the open loan using option `10`.
5. Return the book using option `8`.
6. Export a report using option `12` or `13`.

After issuing a book, it should appear as `Issued` when viewing all books.

## Configuration

There is no separate configuration file.

To change the fine rate or grace period, edit the constants in:

```text
src/main/java/com/vit/library/util/FineCalculator.java
```

The overdue checker runs in the background and scans for overdue loans at regular intervals. Its scan interval can be changed in the overdue checker thread implementation.

## Testing

Run the unit tests with:

```bash
mvn test
```

The tests cover:

- Fine calculation
- ISBN validation
- Loan renewal rules

You can also test the application manually by following the basic workflow above.

When testing manually, check that:

- View All Books shows the correct book status
- View Open Loans shows the issued loan
- Return Book marks the loan as returned
- Any applicable fine is displayed
- Export Catalog Report creates a file under `reports/`
- Export Overdue Report creates a file under `reports/`

## Generated Files

The following files are created while the application runs:

```text
data/library.db
reports/activity.log
reports/*.txt
```

The database and report files are generated at runtime and are excluded from version control through `.gitignore`.

## Notes

- The application is terminal-based.
- No graphical interface is required.
- No separate database server is required.
- SQLite is used as the local database.
- The background overdue checker does not block the main command-line interface.
