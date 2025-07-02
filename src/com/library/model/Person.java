package com.library.model;

/**
 * Abstract base class for all users of the library.
 */
public abstract class Person {
    protected String id;
    protected String name;
    protected String email;
    protected String phone;

    /**
     * Constructs a Person with the given details.
     * @param id    unique identifier
     * @param name  full name
     * @param email contact email
     * @param phone contact phone
     */
    public Person(String id, String name, String email, String phone) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * @return this person's unique ID
     */
    public String getId() { return id; }

    /**
     * @return this person's name
     */
    public String getName() { return name; }

    /**
     * @return this person's email address
     */
    public String getEmail() { return email; }

    /**
     * @return this person's phone number
     */
    public String getPhone() { return phone; }

    /**
     * @return maximum number of books this person can borrow simultaneously
     */
    public abstract int getMaxBorrowLimit();

    /**
     * Prints contact info to stdout.
     */
    public void printInfo() {
        System.out.printf("ID: %s, Name: %s, Email: %s, Phone: %s%n",
                id, name, email, phone);
    }
}
