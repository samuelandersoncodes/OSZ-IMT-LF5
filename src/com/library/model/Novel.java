package com.library.model;

import java.time.Duration;

/**
 * Novel with a 21-day loan period.
 */
public class Novel extends Book {
    public Novel(String bookId, String title, String author, String subject) {
        super(bookId, title, author, subject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getLoanPeriod() {
        return Duration.ofDays(21);
    }
}
