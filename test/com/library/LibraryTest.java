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

    /**
     * Verifies adding and removing books from the catalog.
     */
    @Test
    void testAddAndRemoveBook() {
        TextBook extra = new TextBook("BK-999","Extra","X","S");
        lib.addBook(extra);
        assertEquals(4, lib.listBooks().size(), "Should have 4 books after adding one");
        Book removed = lib.removeBook("BK-999");
        assertSame(extra, removed, "removeBook should return the exact instance");
        assertNull(lib.removeBook("NO-SUCH"), "Removing unknown ID should return null");
    }

    /**
     * Searches case-insensitively by title, author and ID.
     */
    @Test
    void testSearchBooks() {
        List<Book> byTitle = lib.searchBooks("java");
        assertEquals(3, byTitle.size(), "Three books contain 'java' in title");
        List<Book> byAuthor = lib.searchBooks("Brown");
        assertEquals(1, byAuthor.size());
        assertEquals("BK-002", byAuthor.get(0).getBookId());
        List<Book> byId = lib.searchBooks("BK-003");
        assertEquals(1, byId.size());
        assertEquals(reference, byId.get(0));
    }

    /**
     * Ensures listing books on an empty catalog returns an empty collection.
     */
    @Test
    void testListBooksEmpty() {
        Library emptyLib = new Library();
        assertTrue(emptyLib.listBooks().isEmpty(), "New library should start with zero books");
    }

    /**
     * Registers borrowers and librarians and lists them correctly.
     */
    @Test
    void testRegisterUsersAndList() {
        long borrowers = lib.listUsers().stream().filter(u -> u instanceof Borrower).count();
        long librarians = lib.listUsers().stream().filter(u -> u instanceof Librarian).count();
        assertEquals(1, borrowers);
        assertEquals(1, librarians);
        lib.registerBorrower(new Borrower("BOR-0002","Alex","alex@mail.com","557"));
        assertEquals(2,
                lib.listUsers().stream().filter(u -> u instanceof Borrower).count());
    }

    /**
     * Issues and returns books, verifying state changes and invalid operations.
     */
    @Test
    void testIssueAndReturnBehavior() {
        assertTrue(lib.issueBook("BOR-0001","BK-001","LIB-0001"));
        assertTrue(textbook.isIssued(), "Book should be marked issued after issueBook");
        assertFalse(lib.issueBook("BOR-0001","BK-001","LIB-0001"));
        assertTrue(lib.returnBook("BK-001"), "returnBook should succeed when book is issued");
        assertFalse(textbook.isIssued(), "Book should no longer be issued after return");
        assertFalse(lib.returnBook("BK-001"), "Cannot return a book that is not issued");
    }

    /**
     * Ensures a hold cannot be placed on an available book, then can when issued.
     */
    @Test
    void testPlaceHoldAndListHolds() {
        assertFalse(lib.placeHold("BOR-0001","BK-002"),
                "Cannot place hold on book that is not issued yet");
        lib.issueBook("BOR-0001","BK-002","LIB-0001");
        assertTrue(lib.placeHold("BOR-0001","BK-002"));
        List<HoldRequest> holds = lib.getHoldsForBook("BK-002");
        assertEquals(1, holds.size());
        assertEquals(borrower, holds.get(0).getBorrower());
    }

    /**
     * Detects overdue loans by manipulating the due date.
     */
    @Test
    void testOverdueDetection() {
        lib.issueBook("BOR-0001","BK-003","LIB-0001");
        Loan loan = borrower.getLoans().get(0);
        loan.setDueDate(LocalDateTime.now().minusDays(5));
        List<Loan> overdue = lib.displayOverdue();
        assertEquals(1, overdue.size(), "Should detect exactly one overdue loan");
        assertTrue(overdue.get(0).isOverdue());
    }

    /**
     * Verifies renewing loans extends due date and forbids renewing overdue loans.
     */
    @Test
    void testRenewalSuccessAndFailure() {
        lib.issueBook("BOR-0001","BK-001","LIB-0001");
        Loan loan = borrower.getLoans().get(0);
        LocalDateTime originalDue = loan.getDueDate();
        assertTrue(lib.renewLoan("BOR-0001","BK-001"));
        assertTrue(loan.getDueDate().isAfter(originalDue),
                "Due date should be extended after renewLoan");
        loan.setDueDate(LocalDateTime.now().minusDays(1));
        assertFalse(lib.renewLoan("BOR-0001","BK-001"),
                "Cannot renew a loan that is already overdue");
    }

    /**
     * Ensures generateUserReport returns an empty list initially,
     * then contains exactly one Loan after issuing a book.
     */
    @Test
    void testGenerateUserReport() {
        assertTrue(lib.generateUserReport("BOR-0001").isEmpty());
        lib.issueBook("BOR-0001","BK-001","LIB-0001");
        List<Loan> report = lib.generateUserReport("BOR-0001");
        assertEquals(1, report.size());
        assertEquals("BK-001", report.get(0).getBook().getBookId());
    }

    /**
     * Verifies that issueBook and returnBook properly reject
     * non-existent borrower, book or librarian IDs.
     */
    @Test
    void testInvalidIDsInIssueAndReturn() {
        assertFalse(lib.issueBook("NOPE","BK-001","LIB-0001"));
        assertFalse(lib.issueBook("BOR-0001","NOPE","LIB-0001"));
        assertFalse(lib.issueBook("BOR-0001","BK-001","NOPE"));
        assertFalse(lib.returnBook("NOPE"));
    }

    /**
     * Ensures first‐in‐line hold gets the next available copy.
     */
    @Test
    void testBorrowLimitEnforced() {
        // BOR-0001 already registered in @BeforeEach
        // we have 3 seeded books: BK-001, BK-002, BK-003
        assertTrue(lib.issueBook("BOR-0001","BK-001","LIB-0001"));
        assertTrue(lib.issueBook("BOR-0001","BK-002","LIB-0001"));
        assertTrue(lib.issueBook("BOR-0001","BK-003","LIB-0001"));
        // add a fourth book and attempt checkout
        TextBook extra = new TextBook("BK-004","Extra Book","X","Test");
        lib.addBook(extra);
        assertFalse(lib.issueBook("BOR-0001","BK-004","LIB-0001"),
                "Borrower should be refused on 4th checkout beyond their limit");
    }

    /**
     * Ensures first‐in‐line hold gets the next available copy.
     */
    @Test
    void testHoldQueuePriority() {
        // register a second borrower
        Borrower second = new Borrower("BOR-0002","Alex","alex@mail.com","557");
        lib.registerBorrower(second);
        // issue BK-003 to first borrower
        assertTrue(lib.issueBook("BOR-0001","BK-003","LIB-0001"));
        // both place holds
        assertTrue(lib.placeHold("BOR-0001","BK-003"));   // Sam goes first
        assertTrue(lib.placeHold("BOR-0002","BK-003"));   // Alex waits
        // return the book
        assertTrue(lib.returnBook("BK-003"));
        // now only the first hold (Sam) should get it
        assertTrue(lib.issueBook("BOR-0001","BK-003","LIB-0001"),
                "First hold in queue should be able to issue immediately after return");
        // second still cannot issue until Sam returns again
        assertFalse(lib.issueBook("BOR-0002","BK-003","LIB-0001"),
                "Second in queue must wait until first borrower returns");
    }

    /**
     * placeHold(...) must reject non-existent borrower or book IDs.
     */
    @Test
    void testInvalidHoldRequests() {
        assertFalse(lib.placeHold("NO-SUCH","BK-001"));
        assertFalse(lib.placeHold("BOR-0001","NO-SUCH"));
    }

    /**
     * generateUserReport(...) only returns active loans, not past returned ones.
     */
    @Test
    void testGenerateUserReportActiveOnly() {
        // no active loans initially
        assertTrue(lib.generateUserReport("BOR-0001").isEmpty());
        // issue then return
        assertTrue(lib.issueBook("BOR-0001","BK-001","LIB-0001"));
        assertTrue(lib.returnBook("BK-001"));
        // active report is again empty
        assertTrue(lib.generateUserReport("BOR-0001").isEmpty(),
                "Returned loans should not appear in the active‐only report");
    }
}
