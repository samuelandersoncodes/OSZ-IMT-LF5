package com.library.model;

import java.time.Duration;

/**
 * Textbook with a 14-day loan period.
 */
public class TextBook extends Book {
    public TextBook(String bookId, String title, String author, String subject) {
        super(bookId, title, author, subject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getLoanPeriod() {
        return Duration.ofDays(14);
    }
}
