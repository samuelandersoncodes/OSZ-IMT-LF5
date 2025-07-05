package com.library;

import com.library.model.*;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private Library lib;
    private Borrower borrower;
    private Librarian librarian;
    private TextBook textbook;
    private Novel novel;
    private Reference reference;

    @BeforeEach
    void setUp() {
        lib = new Library();

        // seed one borrower and one librarian
        borrower  = new Borrower("BOR-0001", "Sam",  "sam@mail.com",  "555");
        librarian = new Librarian("LIB-0001","Mueller","mu@mail.com","556");
        lib.registerBorrower(borrower);
        lib.registerLibrarian(librarian);

        // seed three books
        textbook  = new TextBook  ("BK-001","Java Fundamentals","Evans","CS");
        novel     = new Novel     ("BK-002","The Java Saga",       "Brown","Fantasy");
        reference = new Reference ("BK-003","Java API Handbook",   "Sun","Vol1");
        lib.addBook(textbook);
        lib.addBook(novel);
        lib.addBook(reference);
    }
}
