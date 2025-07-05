package com.library;

import com.library.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service handling books, users, holds, and loans.
 */
public class Library {
    private final Map<String, Book>   catalog     = new HashMap<>();
    private final Map<String, Person> users       = new HashMap<>();
    private final List<HoldRequest>   holdQueue   = new ArrayList<>();
    private final List<Loan>          activeLoans = new ArrayList<>();

    // Book operations

    /**
     * Adds a book to the catalog.
     * @param b the Book to add
     */
    public void addBook(Book b) {
        catalog.put(b.getBookId(), b);
    }

    /**
     * Removes a book from the catalog.
     * @param bookId unique ID of the book
     * @return the removed Book, or null if none existed
     */
    public Book removeBook(String bookId) {
        return catalog.remove(bookId);
    }

    /**
     * Searches books by title, author, or ID.
     * @param keyword search term
     * @return matching list of books
     */
    public List<Book> searchBooks(String keyword) {
        String k = keyword.toLowerCase();
        return catalog.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(k)
                        || b.getAuthor().toLowerCase().contains(k)
                        || b.getBookId().equalsIgnoreCase(keyword))
                .collect(Collectors.toList());
    }

    /**
     * @return all books in the catalog
     */
    public Collection<Book> listBooks() {
        return catalog.values();
    }

    // User operations

    /**
     * Registers a new borrower.
     * @param b the Borrower to add
     */
    public void registerBorrower(Borrower b) {
        users.put(b.getId(), b);
    }

    /**
     * Registers a new librarian.
     * @param l the Librarian to add
     */
    public void registerLibrarian(Librarian l) {
        users.put(l.getId(), l);
    }

    /**
     * @return all registered users (borrowers + librarians)
     */
    public Collection<Person> listUsers() {
        return users.values();
    }

    // Hold operations

    /**
     * Places a hold request for a borrowed book.
     * @param borrowerId ID of the borrower
     * @param bookId     ID of the book
     * @return true if the hold was placed
     */
    public boolean placeHold(String borrowerId, String bookId) {
        Person p = users.get(borrowerId);
        Book   b = catalog.get(bookId);
        if (!(p instanceof Borrower) || b == null || !b.isIssued()) return false;
        Borrower brw = (Borrower) p;
        HoldRequest hr = new HoldRequest(brw, b);
        holdQueue.add(hr);
        brw.getHolds().add(hr);
        return true;
    }

    /**
     * @param bookId ID of the book
     * @return list of hold requests for that book
     */
    public List<HoldRequest> getHoldsForBook(String bookId) {
        return holdQueue.stream()
                .filter(hr -> hr.getBook().getBookId().equals(bookId))
                .collect(Collectors.toList());
    }

    // Circulation operations

    /**
     * Issues a book to a borrower, if valid.
     * @param borrowerId  ID of the borrower
     * @param bookId      ID of the book
     * @param librarianId ID of the librarian processing
     * @return true if issued successfully
     */
    public boolean issueBook(String borrowerId, String bookId, String librarianId) {
        Person pu = users.get(borrowerId);
        Person pl = users.get(librarianId);
        Book   bk = catalog.get(bookId);
        if (!(pu instanceof Borrower) || !(pl instanceof Librarian) || bk == null || bk.isIssued())
            return false;

        Borrower brw = (Borrower) pu;
        Librarian lib = (Librarian) pl;

        // enforce borrow limit
        long current = brw.getLoans().stream().filter(l -> !l.isOverdue()).count();
        if (current >= brw.getMaxBorrowLimit()) return false;

        // enforce hold queue order
        var holds = getHoldsForBook(bookId);
        if (!holds.isEmpty() && !holds.get(0).getBorrower().equals(brw)) return false;

        Loan loan = new Loan(brw, bk, lib);
        bk.issue(loan);
        activeLoans.add(loan);
        brw.getLoans().add(loan);
        holdQueue.removeIf(hr -> hr.getBorrower().equals(brw) && hr.getBook().equals(bk));
        return true;
    }

    /**
     * Processes return of a book.
     * @param bookId ID of the book
     * @return true if returned successfully
     */
    public boolean returnBook(String bookId) {
        Book bk = catalog.get(bookId);
        if (bk == null || !bk.isIssued()) return false;
        Optional<Loan> ol = activeLoans.stream()
                .filter(l -> l.getBook().equals(bk))
                .findFirst();
        if (ol.isEmpty()) return false;
        Loan loan = ol.get();
        loan.close();
        activeLoans.remove(loan);
        return true;
    }

    /**
     * Renews a loan only if it exists and is not overdue.
     * @param borrowerId ID of the borrower
     * @param bookId     ID of the book
     * @return true if renewed ; false otherwise
     */
    public boolean renewLoan(String borrowerId, String bookId) {
        for (Loan loan : activeLoans) {
            if (loan.getBorrower().getId().equals(borrowerId)
                    && loan.getBook().getBookId().equals(bookId)) {
                if (loan.isOverdue()) {
                    return false;
                }
                loan.renew();
                return true;
            }
        }
        return false;
    }

    // Reports

    /**
     * @return list of currently overdue loans
     */
    public List<Loan> displayOverdue() {
        return activeLoans.stream().filter(Loan::isOverdue).collect(Collectors.toList());
    }

    /**
     * @param borrowerId ID of the borrower
     * @return all loans (active and past) for that borrower
     */
    public List<Loan> generateUserReport(String borrowerId) {
        return activeLoans.stream()
                .filter(l -> l.getBorrower().getId().equals(borrowerId))
                .collect(Collectors.toList());
    }

    /**
     * Look up a book by its ID.
     * @param bookId the book's unique identifier
     * @return the Book from the catalog, or null if not found
     */
    public Book getBook(String bookId) {
        return catalog.get(bookId);
    }
}
