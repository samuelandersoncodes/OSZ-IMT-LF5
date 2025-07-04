package com.library;

import com.library.model.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // seed demo data
        Library lib = new Library();

        // seed borrowers
        lib.registerBorrower(new Borrower("BOR-0001",     "Sam",     "sam@mail.com",     "1234567555"));
        lib.registerBorrower(new Borrower("BOR-0002",   "Ruurd",   "ruurd@mail.com",   "1234567556"));
        lib.registerBorrower(new Borrower("BOR-0003", "Michael", "mich@mail.com",    "1234567557"));

        // seed librarians
        lib.registerLibrarian(new Librarian("LIB-0001", "Mueller", "mul@mail.com", "1234567558"));
        lib.registerLibrarian(new Librarian("LIB-0002",   "Weber",   "web@mail.com", "1234567559"));
        lib.registerLibrarian(new Librarian("LIB-0003",   "Sachs",   "sac@mail.com", "1234567560"));

        // seed books
        lib.addBook(new TextBook("101","Java Fundamentals","Evans","CS"));
        lib.addBook(new Novel    ("202","The Java Saga","Brown","Fantasy"));
        lib.addBook(new Reference("303","Java API Handbook","Sun","Vol1"));

        // demo transaction
        lib.placeHold("sam","101");
        lib.issueBook("sam","101","mueller");

        // start CLI
        new LibrarySystem(lib, new Scanner(System.in)).run();
    }
}
