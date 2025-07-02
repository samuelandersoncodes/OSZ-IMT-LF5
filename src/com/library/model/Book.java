package com.library.model;

import java.time.Duration;

/**
 * Abstract representation of a library book.
 */
public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected String subject;
    protected boolean isIssued = false;
    protected Loan currentLoan;

    /**
     * Constructs a Book.
     * @param bookId  unique book ID
     * @param title   title
     * @param author  author name
     * @param subject subject/genre
     */
    public Book(String bookId, String title, String author, String subject) {
        this.bookId  = bookId;
        this.title   = title;
        this.author  = author;
        this.subject = subject;
    }

    /** @return the unique book ID */
    public String getBookId() { return bookId; }

    /** @return the title of the book */
    public String getTitle() { return title; }

    /** @return the author name */
    public String getAuthor() { return author; }

    /** @return the subject or genre */
    public String getSubject() { return subject; }

    /** @return true if currently issued */
    public boolean isIssued() { return isIssued; }

    /**
     * @return loan period for this book type
     */
    public abstract Duration getLoanPeriod();

    /**
     * Marks this book as issued and records the loan.
     * @param loan the Loan object
     */
    public void issue(Loan loan) {
        this.isIssued    = true;
        this.currentLoan = loan;
    }

    /**
     * Marks this book as returned.
     */
    public void returned() {
        this.isIssued    = false;
        this.currentLoan = null;
    }

    /**
     * Prints a summary of this book.
     */
    public void printInfo() {
        System.out.printf("[%s] %s by %s (ID: %s)%s%n",
                getClass().getSimpleName(),
                title, author, bookId,
                isIssued ? " [OUT]" : "");
    }
}
