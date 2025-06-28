package com.library;

import com.library.model.*;
import java.util.Scanner;

public class Main {
    private static Library library = new Library();
    public static void main(String[] args) {
        initSampleData();
        LibrarySystem cli = new LibrarySystem(library, new Scanner(System.in));
        cli.run();
    }

    private static void initSampleData() {
        library.registerBorrower(new Borrower("B1","Sam","",""));
        library.registerBorrower(new Borrower("B2","Ruurd","",""));
        library.registerBorrower(new Borrower("B3","Michael","",""));
        library.registerLibrarian(new Librarian("L1","Mueller","",""));
        library.registerLibrarian(new Librarian("L2","Weber","",""));
        library.registerLibrarian(new Librarian("L3","Sachs","",""));
        library.addBook(new TextBook("TB1","Intro to OOP","Author A","Programming"));
        library.addBook(new Novel("N1","The Great Novel","Author B","Fiction"));
        library.addBook(new Reference("R1","Encyclopedia of Science","Author C","Reference"));
    }
}