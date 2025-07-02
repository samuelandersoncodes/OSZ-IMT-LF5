package com.library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A library patron who can place holds and borrow books.
 */
public class Borrower extends Person {
    private final List<Loan> loans = new ArrayList<>();
    private final List<HoldRequest> holds = new ArrayList<>();

    /**
     * Constructs a Borrower.
     * @param id    unique ID
     * @param name  borrower name
     * @param email contact email
     * @param phone contact phone
     */
    public Borrower(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxBorrowLimit() {
        return 3;
    }

    /**
     * @return list of active and past loans
     */
    public List<Loan> getLoans() {
        return loans;
    }

    /**
     * @return list of pending hold requests
     */
    public List<HoldRequest> getHolds() {
        return holds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printInfo() {
        System.out.printf("Borrower: %s (ID: %s)%n", name, id);
    }
}
