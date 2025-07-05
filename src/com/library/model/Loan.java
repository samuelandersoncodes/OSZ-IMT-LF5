package com.library.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Tracks a book checkout transaction.
 */
public class Loan {
    private final LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private final Borrower borrower;
    private final Book book;
    private final Librarian processedBy;

    /**
     * Creates a new Loan.
     * @param borrower    who borrows
     * @param book        book being borrowed
     * @param processedBy librarian issuing the loan
     */
    public Loan(Borrower borrower, Book book, Librarian processedBy) {
        this.issueDate   = LocalDateTime.now();
        this.dueDate     = issueDate.plus(book.getLoanPeriod());
        this.borrower    = borrower;
        this.book        = book;
        this.processedBy = processedBy;
    }

    /** @return true if past due and not returned */
    public boolean isOverdue() {
        return returnDate == null && LocalDateTime.now().isAfter(dueDate);
    }

    /** Extends the due date by one loan period */
    public void renew() {
        this.dueDate = this.dueDate.plus(book.getLoanPeriod());
    }

    /** Marks the loan as returned and updates the book */
    public void close() {
        this.returnDate = LocalDateTime.now();
        book.returned();
    }

    /** @return the borrower */
    public Borrower getBorrower() { return borrower; }

    /** @return the book */
    public Book getBook() { return book; }

    /** @return the due date */
    public LocalDateTime getDueDate() { return dueDate; }

    /** Prints a summary of the loan */
    public void printInfo() {
        System.out.printf("Loan: %s → %s, due %s%n",
                borrower.getName(),
                book.getTitle(),
                dueDate.toLocalDate());
    }

    /**
     * force‐set the due date for test.
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
