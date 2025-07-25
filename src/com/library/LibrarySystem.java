package com.library;

import com.library.model.*;

import java.util.Scanner;

import java.util.Optional;

/**
 * Command‐line interface handling user interaction.
 */
public class LibrarySystem {
    private final Library lib;
    private final Scanner in;

    // sequence counters for auto-IDs
    private int bookSeq;
    private int borrowerSeq;
    private int librarianSeq;

    /**
     * @param lib library service
     * @param in  input scanner (e.g. System.in)
     */
    public LibrarySystem(Library lib, Scanner in) {
        this.lib = lib;
        this.in  = in;

        /*
        * Count seeded books so we start at the next available ID,
        * initialize ID sequence counters by counting already-registered users,
        * so new auto-generated IDs won’t collide with seeded entries
        */
        this.bookSeq = lib.listBooks().size();
        this.borrowerSeq = (int) lib.listUsers().stream().filter(u -> u instanceof Borrower).count();
        this.librarianSeq = (int) lib.listUsers().stream().filter(u -> u instanceof Librarian).count();
    }

    /**
     * Starts the main menu loop.
     */
    public void run() {
        while (true) {
            printMenu();
            switch (in.nextLine().trim()) {
                case "1"  -> addBook();
                case "2"  -> removeBook();
                case "3"  -> searchBooks();
                case "4"  -> listBooks();
                case "5"  -> registerBorrower();
                case "6"  -> registerLibrarian();
                case "7"  -> listUsers();
                case "8"  -> placeHold();
                case "9"  -> listHolds();
                case "10" -> issueBook();
                case "11" -> renewLoan();
                case "12" -> returnBook();
                case "13" -> displayOverdue();
                case "14" -> userReport();
                case "0"  -> { System.out.println("Good-bye"); return; }
                default   -> System.out.println("❌ Invalid option");
            }
        }
    }

    /** Prints the main menu to stdout. */
    private static final String ANSI_RESET  = "\u001B[0m";
    private static final String ANSI_BLUE   = "\u001B[34m";
    private static final String ANSI_CYAN   = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private void printMenu() {
        int width = 64;                               // total inner width
        String hor = "─".repeat(width);
        // Box top
        System.out.println(ANSI_CYAN + "┌" + hor + "┐" + ANSI_RESET);

        // Title
        String title = "📚 LIBRARY MANAGEMENT SYSTEM 📚";
        System.out.println(ANSI_CYAN + "│" +
                center(title, width) +
                "│" + ANSI_RESET);

        // Separator
        System.out.println(ANSI_CYAN + "├" + hor + "┤" + ANSI_RESET);

        // Menu items
        String[][] items = {
                {"1) Add Book",            "8)  Place Hold"},
                {"2) Remove Book",         "9)  List Holds for Book"},
                {"3) Search Books",        "10) Issue Book"},
                {"4) Display All Books",   "11) Renew Loan"},
                {"5) Register Borrower",   "12) Return Book"},
                {"6) Register Librarian",  "13) Overdue Loans"},
                {"7) Display All Users",   "14) Borrower History"},
        };

        // Print two columns
        int colWidth = (width - 3) / 2;  // leave 1 space padding and 1 between columns
        for (var row : items) {
            String left  = padRight(row[0], colWidth);
            String right = padRight(row[1], colWidth);
            System.out.println(ANSI_CYAN + "│ "
                    + ANSI_BLUE + left  + ANSI_RESET + " "
                    + ANSI_BLUE + right + ANSI_RESET
                    + " │");
        }

        // Separator before exit
        System.out.println(ANSI_CYAN + "├" + hor + "┤" + ANSI_RESET);

        // Exit centered
        String exit = "0) Exit";
        System.out.println(ANSI_CYAN + "│"
                + center(exit, width)
                + "│" + ANSI_RESET);

        // Box bottom
        System.out.println(ANSI_CYAN + "└" + hor + "┘" + ANSI_RESET);

        // Prompt
        System.out.print(ANSI_YELLOW + "Choice> " + ANSI_RESET);
    }

    /** Centers s in a field of width w, padding with spaces. */
    private static String center(String s, int w) {
        if (s.length() >= w) return s.substring(0, w);
        int pad = (w - s.length()) / 2;
        return " ".repeat(pad) + s + " ".repeat(w - pad - s.length());
    }

    /** Pads s on the right with spaces to exactly width w. */
    private static String padRight(String s, int w) {
        if (s.length() >= w) return s.substring(0, w);
        return s + " ".repeat(w - s.length());
    }

    /**
     * Prompts the user step-by-step to add a new book.
     * If an unknown type is entered, prints an error and returns to main menu.
     */
    private void addBook() {
        System.out.print("Title: ");
        String title = in.nextLine().trim();
        System.out.print("Author: ");
        String author = in.nextLine().trim();
        System.out.print("Subject: ");
        String subject = in.nextLine().trim();
        System.out.print("Type (1=TextBook, 2=Novel, 3=Reference): ");
        String type = in.nextLine().trim();
        String bookId = String.format("BK-%03d", ++bookSeq);
        Book book;
        switch (type) {
            case "1" -> book = new TextBook(bookId, title, author, subject);
            case "2" -> book = new Novel    (bookId, title, author, subject);
            case "3" -> book = new Reference(bookId, title, author, subject);
            default  -> {
                System.out.println("❌ Invalid type. Returning to main menu...");
                return;
            }
        }
        lib.addBook(book);
        System.out.println("✅ Book: " + title + " with ID : " + bookId + " added successfully");
    }

    /** Prompts for a book ID and removes it. */
    private void removeBook() {
        System.out.print("Book ID: ");
        String id = in.nextLine().trim();
        Book removed = lib.removeBook(id);
        if (removed != null) {
            System.out.println("✅ Book: " + removed.getTitle() + " is removed");
        } else {
            System.out.println("❌ No book with ID: " + id + " was found");
            return;
        }
    }

    /**
     * Prompts for a search keyword, displays matching books,
     * or shows an error and returns if no matches are found.
     */
    private void searchBooks() {
        System.out.print("Keyword: ");
        String keyword = in.nextLine().trim();
        var results = lib.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("❌ No book(s) found matching \"" + keyword + "\". Returning to main menu...");
            return;
        }
        results.forEach(Book::printInfo);
    }

    /** Lists all books in the catalog. */
    private void listBooks() {
        var books = lib.listBooks();
        if (books.isEmpty()) {
            System.out.println("ℹ️  There are currently no books in the catalog. Returning to main menu...");
            return;
        }
        books.forEach(Book::printInfo);
    }

    /**
     * Registers a new borrower, prompting for name, email and phone,
     * then auto-generating an ID like BOR-0004, etc.
     */
    private void registerBorrower() {
        System.out.print("Name: ");
        String name = in.nextLine().trim();
        System.out.print("Email: ");
        String email = in.nextLine().trim();
        System.out.print("Phone: ");
        String phone = in.nextLine().trim();
        String id = String.format("BOR-%04d", ++borrowerSeq);
        lib.registerBorrower(new Borrower(id, name, email, phone));
        System.out.println("✅ Borrower " + name + " registered with ID " + id);
    }

    /**
     * Registers a new librarian, prompting for name, email and phone,
     * then auto-generating an ID like LIB-0004, etc.
     */
    private void registerLibrarian() {
        System.out.print("Name: ");
        String name = in.nextLine().trim();
        System.out.print("Email: ");
        String email = in.nextLine().trim();
        System.out.print("Phone: ");
        String phone = in.nextLine().trim();
        String id = String.format("LIB-%04d", ++librarianSeq);
        lib.registerLibrarian(new Librarian(id, name, email, phone));
        System.out.println("✅ Librarian " + name + " registered with ID " + id);
    }

    /** Displays all registered users. */
    private void listUsers() {
        lib.listUsers().forEach(Person::printInfo);
    }

    /**
     * Places a hold for a borrower on a book.
     * Only books currently issued can be placed on hold.
     */
    private void placeHold() {
        System.out.print("Borrower ID: ");
        String borrowerId = in.nextLine().trim();
        System.out.print("Book ID: ");
        String bookId = in.nextLine().trim();
        boolean success = lib.placeHold(borrowerId, bookId);
        if (success) {
            System.out.println("✅ Hold placed for book ID : " + bookId);
        } else {
            System.out.println("❌ Failed! only books that are currently issued can be placed on hold.");
        }
    }

    /**
     * Prompts for a book ID and prints the current hold queue
     * book in the format “Borrower Name  –  Request Date”.
     * If no holds exist, an informational message is shown and the
     * user is returned to the main menu.
     */
    private void listHolds() {
        System.out.print("Book ID: ");
        String bookId = in.nextLine().trim();
        var holds = lib.getHoldsForBook(bookId);
        if (holds.isEmpty()) {
            System.out.println("ℹ️  There are currently no holds on book ID " + bookId + ".");
            return;
        }
        System.out.println("📌 Holds for book " + bookId + ":");
        holds.forEach(hr ->
                System.out.printf(" • %s @ %s%n",
                        hr.getBorrower().getName(),
                        hr.getRequestDate().toLocalDate())
        );
    }

    /** Issues a book to a borrower by a librarian.
     * If it’s already issued, or IDs are invalid, prints an explicit message.
     */
    private void issueBook() {
        System.out.print("Borrower ID: ");
        String borrowerId  = in.nextLine().trim();
        System.out.print("Book ID: ");
        String bookId = in.nextLine().trim();
        System.out.print("Librarian ID: ");
        String librarianId  = in.nextLine().trim();
        Book book = lib.getBook(bookId);
        if (book == null) {
            System.out.println("❌ No book found with ID: " + bookId);
            return;
        }
        if (book.isIssued()) {
            System.out.println("❌ Cannot issue—book " + bookId + " is already issued.");
            return;
        }
        boolean ok = lib.issueBook(borrowerId, bookId, librarianId);
        if (ok) {
            System.out.println("✅ Issued book with ID : " + bookId + " to User with ID : " + borrowerId);
        } else {
            System.out.println("❌ Failed to issue. Check borrower ID, librarian ID, borrow limit, or hold queue.");
        }
    }

    /** Renews an existing loan if eligible. */
    private void renewLoan() {
        System.out.print("Borrower ID: ");
        String borrowerId  = in.nextLine().trim();
        System.out.print("Book ID: ");
        String bookId = in.nextLine().trim();
        Optional<Loan> optLoan = lib.generateUserReport(borrowerId).stream().filter(l -> l.getBook().getBookId().equals(bookId))
                .findFirst();
        if (optLoan.isEmpty()) {
            System.out.println("❌ No active loan for book ID: " + bookId);
            return;
        }
        Loan loan = optLoan.get();
        if (loan.isOverdue()) {
            System.out.println("❌ Cannot renew—loan for book ID " + bookId + " is already overdue since "
                    + loan.getDueDate().toLocalDate());
            return;
        }
        loan.renew();
        System.out.println("✅ Loan renewed! New due date: " + loan.getDueDate().toLocalDate());
    }

    /** Returns a borrowed book. */
    private void returnBook() {
        System.out.print("Book ID: ");
        System.out.println(lib.returnBook(in.nextLine()) ? "✅ Returned" : "❌ Failed");
    }

    /** Displays all overdue loans.
     * informs the user if no overdue.
     **/
    private void displayOverdue() {
        var overdue = lib.displayOverdue();
        if (overdue.isEmpty()) {
            System.out.println("ℹ️ There are no overdue loans at the moment");
            return;
        }
        System.out.println("⚠️  Overdue loans:");
        overdue.forEach(Loan::printInfo);
    }

    /**
     * Shows loan history for a specific borrower.
     * If they have not borrowed anything yet, displays an informational message.
     */
    private void userReport() {
        System.out.print("Borrower ID: ");
        String borrowerId = in.nextLine().trim();
        Person user = lib.listUsers().stream().filter(u -> u.getId().equals(borrowerId)).findFirst().orElse(null);
        if (user == null) {
            System.out.println("❌ No user found with ID: " + borrowerId);
            return;
        }
        if (!(user instanceof Borrower)) {
            System.out.println("❌ User ID " + borrowerId + " is not a borrower.");
            return;
        }
        var history = lib.generateUserReport(borrowerId);
        if (history.isEmpty()) {
            System.out.println("ℹ️  Borrower " + borrowerId + " has not borrowed any books yet.");
            return;
        }
        System.out.println("📜 Borrowing history for " + borrowerId + ":");
        history.forEach(Loan::printInfo);
    }
}
