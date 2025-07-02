package com.library.model;

import java.time.Duration;

/**
 * Reference material, 7-day in-house loan.
 */
public class Reference extends Book {
    public Reference(String bookId, String title, String author, String subject) {
        super(bookId, title, author, subject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getLoanPeriod() {
        return Duration.ofDays(7);
    }
}
