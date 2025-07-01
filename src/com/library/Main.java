package com.library;

import com.library.model.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // seed demo data
        Library lib = new Library();

        // start CLI
        new LibrarySystem(lib, new Scanner(System.in)).run();
    }
}
