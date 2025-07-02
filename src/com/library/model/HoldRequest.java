package com.library.model;

import java.time.LocalDateTime;

/**
 * Represents a patron's reservation request for a book.
 */
public class HoldRequest {
    private final LocalDateTime requestDate;
    private final Borrower borrower;
    private final Book book;

    /**
     * Constructs a HoldRequest.
     * @param borrower who placed the hold
     * @param book     which book is requested
     */
    public HoldRequest(Borrower borrower, Book book) {
        this.requestDate = LocalDateTime.now();
        this.borrower    = borrower;
        this.book        = book;
    }

    /** @return timestamp of the request */
    public LocalDateTime getRequestDate() { return requestDate; }

    /** @return the borrower who requested */
    public Borrower getBorrower() { return borrower; }

    /** @return the book requested */
    public Book getBook() { return book; }
}
