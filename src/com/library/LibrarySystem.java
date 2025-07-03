package com.library;

import com.library.model.*;

import java.util.Scanner;

/**
 * Command‐line interface handling user interaction.
 */
public class LibrarySystem {
    private final Library lib;
    private final Scanner in;

    /**
     * @param lib library service
     * @param in  input scanner (e.g. System.in)
     */
    public LibrarySystem(Library lib, Scanner in) {
        this.lib = lib;
        this.in  = in;
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
        System.out.print("Book ID: ");
        String id = in.nextLine().trim();
        System.out.print("Title: ");
        String title = in.nextLine().trim();
        System.out.print("Author: ");
        String author = in.nextLine().trim();
        System.out.print("Subject: ");
        String subject = in.nextLine().trim();
        System.out.print("Type (1=TextBook, 2=Novel, 3=Reference): ");
        String type = in.nextLine().trim();
        Book book;
        switch (type) {
            case "1":
                book = new TextBook(id, title, author, subject);
                break;
            case "2":
                book = new Novel(id, title, author, subject);
                break;
            case "3":
                book = new Reference(id, title, author, subject);
                break;
            default:
                System.out.println("❌ Invalid input. Returning to main menu...");
                return;
        }
        lib.addBook(book);
        System.out.println("✅ Book: " + book.getTitle() + " added successfully");
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
        lib.listBooks()
                .forEach(Book::printInfo);
    }

    /** Registers a new borrower with minimal info. */
    private void registerBorrower() {
        System.out.print("ID: ");   String id   = in.nextLine();
        System.out.print("Name: "); String name = in.nextLine();
        lib.registerBorrower(new Borrower(id, name, "", ""));
        System.out.println("✅ Borrower registered");
    }

    /** Registers a new librarian with minimal info. */
    private void registerLibrarian() {
        System.out.print("ID: ");   String id   = in.nextLine();
        System.out.print("Name: "); String name = in.nextLine();
        lib.registerLibrarian(new Librarian(id, name, "", ""));
        System.out.println("✅ Librarian registered");
    }

    /** Displays all registered users. */
    private void listUsers() {
        lib.listUsers()
                .forEach(Person::printInfo);
    }

    /** Places a hold for a borrower on a book. */
    private void placeHold() {
        System.out.print("Borrower ID: "); String bid  = in.nextLine();
        System.out.print("Book ID: ");     String bid2 = in.nextLine();
        System.out.println(lib.placeHold(bid, bid2) ? "✅ Hold placed" : "❌ Failed");
    }

    /** Lists hold requests for a given book. */
    private void listHolds() {
        System.out.print("Book ID: ");
        lib.getHoldsForBook(in.nextLine())
                .forEach(hr -> System.out.printf(
                        "%s @ %s%n",
                        hr.getBorrower().getName(),
                        hr.getRequestDate().toLocalDate()));
    }

    /** Issues a book to a borrower by a librarian. */
    private void issueBook() {
        System.out.print("Borrower ID: ");  String bid  = in.nextLine();
        System.out.print("Book ID: ");      String bid2 = in.nextLine();
        System.out.print("Librarian ID: "); String lid  = in.nextLine();
        System.out.println(lib.issueBook(bid, bid2, lid) ? "✅ Issued" : "❌ Failed");
    }

    /** Renews an existing loan if eligible. */
    private void renewLoan() {
        System.out.print("Borrower ID: "); String bid  = in.nextLine();
        System.out.print("Book ID: ");     String bid2 = in.nextLine();
        System.out.println(lib.renewLoan(bid, bid2) ? "✅ Renewed" : "❌ Failed");
    }

    /** Returns a borrowed book. */
    private void returnBook() {
        System.out.print("Book ID: ");
        System.out.println(lib.returnBook(in.nextLine()) ? "✅ Returned" : "❌ Failed");
    }

    /** Displays all overdue loans. */
    private void displayOverdue() {
        lib.displayOverdue()
                .forEach(Loan::printInfo);
    }

    /** Shows loan history for a specific borrower. */
    private void userReport() {
        System.out.print("Borrower ID: ");
        lib.generateUserReport(in.nextLine())
                .forEach(Loan::printInfo);
    }
}
