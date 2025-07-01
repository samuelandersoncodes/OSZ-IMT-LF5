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
    private void printMenu() {
        System.out.println("""
            \n=== Library System ===
            1) Add Book      2) Remove Book    3) Search Books   4) Display All Books
            5) Register Borrower    6) Register Librarian    7) Display All Users
            8) Place Hold    9) List Holds for Book
            10) Issue Book   11) Renew Loan    12) Return Book   13) Overdue Loans
            14) Borrower History
            0) Exit
            """);
        System.out.print("Choice> ");
    }

    /** Prompts for details and adds a new book. */
    private void addBook() {
        System.out.print("Book ID: ");      String id     = in.nextLine();
        System.out.print("Title: ");        String title  = in.nextLine();
        System.out.print("Author: ");       String author = in.nextLine();
        System.out.print("Subject: ");      String subj   = in.nextLine();
        System.out.print("Type (1=TextBook,2=Novel,3=Reference): ");
        String t = in.nextLine();
        Book b = switch (t) {
            case "1" -> new TextBook(id, title, author, subj);
            case "2" -> new Novel(id, title, author, subj);
            case "3" -> new Reference(id, title, author, subj);
            default  -> null;
        };
        if (b != null) {
            lib.addBook(b);
            System.out.println("✅ Book added");
        } else {
            System.out.println("❌ Unknown type");
        }
    }

    /** Prompts for a book ID and removes it. */
    private void removeBook() {
        System.out.print("Book ID: ");
        lib.removeBook(in.nextLine());
        System.out.println("✅ If it existed, it's gone.");
    }

    /** Prompts for a keyword and searches the catalog. */
    private void searchBooks() {
        System.out.print("Keyword: ");
        lib.searchBooks(in.nextLine())
                .forEach(Book::printInfo);
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
