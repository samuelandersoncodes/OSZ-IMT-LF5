package com.library.model;

/**
 * Library staff member with higher borrow privileges.
 */
public class Librarian extends Person {
    /**
     * Constructs a Librarian.
     * @param id    unique ID
     * @param name  librarian name
     * @param email contact email
     * @param phone contact phone
     */
    public Librarian(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxBorrowLimit() {
        return 10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printInfo() {
        System.out.printf("Librarian: %s (ID: %s)%n", name, id);
    }
}
