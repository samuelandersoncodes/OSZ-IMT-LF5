Library Management System - Project Plan
1. Project Overview
   A terminal-based Library Management System that demonstrates core OOP principles through managing books, users and borrowing operations.

2. Core Features
   Book Management: Add, remove, search, and display books
   User Management: Register students and staff with different privileges
   Borrowing System: Borrow/return books with due dates
   Search Functionality: Find books by title, author, or ISBN
   Reports: Display borrowed books, overdue items, user statistics

3. OOP Principles Demonstrated
   Inheritance
   Person → Student, Staff
   Book → TextBook, Novel, Reference
   User → Student, Librarian
   Polymorphism
   Different user types have different borrowing limits
   Various book types have different loan periods
   Method overriding for display and validation
   Encapsulation
   Private fields with getter/setter methods
   Data validation in constructors and setters
   Protected access for inheritance
   Abstraction
   Abstract Person class with concrete implementations
   Interface Borrowable for items that can be borrowed
   Abstract methods for user-specific operations

4. Class Structure
   Core Classes
   Person (Abstract)
   Fields: name, id, email, phone
   Abstract methods: getMaxBorrowLimit(), getDisplayInfo()
   Student (extends Person)
   Additional: studentId, semester
   Lower borrowing limit (3 books)
   Staff (extends Person)
   Additional: department, position
   Higher borrowing limit (10 books)
   Book (Abstract)
   Fields: title, author, isbn, isAvailable
   Abstract methods: getLoanPeriod(), getBookType()
   TextBook (extends Book)
   Additional: subject, edition
   14-day loan period
   Novel (extends Book)
   Additional: genre, pages
   21-day loan period
   Reference (extends Book)
   Additional: volume, edition
   7-day loan period (reference material)
   Library
   Manages collections of books and users
   Handles borrowing/returning operations
   BorrowRecord
   Tracks borrowing transactions
   Fields: user, book, borrowDate, dueDate, returnDate
   LibrarySystem
   Main class with user interface
   Menu system for all operations

5. Key Methods by Class
   Library Class
   addBook(Book book)
   removeBook(String isbn)
   searchBooks(String query)
   registerUser(Person user)
   borrowBook(String isbn, String userId)
   returnBook(String isbn, String userId)
   displayOverdueBooks()
   generateUserReport(String userId)
   Person Classes
   getMaxBorrowLimit() - Abstract method
   getDisplayInfo() - Abstract method
   canBorrow() - Check borrowing eligibility
   Book Classes
   getLoanPeriod() - Abstract method
   getBookType() - Abstract method
   getDisplayInfo() - Book details

6. Data Structures Used
   ArrayList<Book> for book collection
   ArrayList<Person> for user collection
   ArrayList<BorrowRecord> for tracking loans
   HashMap<String, Person> for quick user lookup
   HashMap<String, Book> for quick book lookup

7. Menu Structure

(a) Book Management
   1.1 Add Book
   1.2 Remove Book
   1.3 Search Books
   1.4 Display All Books

(b) User Management
   2.1 Register Student
   2.2 Register Staff
   2.3 Display All Users

(c) Borrowing Operations
   3.1 Borrow Book
   3.2 Return Book
   3.3 Display Borrowed Books
   3.4 Display Overdue Books

(d) Reports
   4.1 User Borrowing History
   4.2 Popular Books
   4.3 Library Statistics

(e) Exit

8. Sample Data for Testing
   Pre-populate with sample books and users
   Include different book types and user types
   Some books already borrowed to test various scenarios

9. Error Handling
   Invalid ISBN format validation
   Duplicate book/user prevention
   Borrowing limit enforcement
   Book availability checking
   Input validation for all user inputs

10. Advanced Features (Optional Extensions)
    Book reservation system
    Fine calculation for overdue books
    Book recommendation based on borrowing history
    Export reports to text files
    Simple backup/restore functionality

11. Project Structure
    src/
    ├── main/
    │   ├── LibrarySystem.java (Main class)
    │   ├── models/
    │   │   ├── Person.java
    │   │   ├── Student.java
    │   │   ├── Staff.java
    │   │   ├── Book.java
    │   │   ├── TextBook.java
    │   │   ├── Novel.java
    │   │   ├── Reference.java
    │   │   └── BorrowRecord.java
    │   ├── services/
    │   │   └── Library.java
    │   └── utils/
    │       ├── InputValidator.java
    │       └── DateHelper.java

    

