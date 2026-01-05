package com.example.library_management.auth.controller;

import com.example.library_management.auth.dto.AddBookRequest;
import com.example.library_management.auth.dto.AdminBorrowRequest;
import com.example.library_management.auth.dto.ExtendDueDateRequest;
import com.example.library_management.auth.dto.UpdateUserRequest;
import com.example.library_management.auth.dto.UpdateBookRequest;
import com.example.library_management.auth.entity.Book;
import com.example.library_management.auth.entity.BorrowRecord;
import com.example.library_management.auth.entity.User;
import com.example.library_management.auth.repository.BookRepository;
import com.example.library_management.auth.repository.BorrowRepository;
import com.example.library_management.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN','STAFF','LIB_STAFF')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final BorrowRepository borrowRepo;
    private final BookRepository bookRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> allUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/users/{id}/borrows")
    public List<BorrowRecord> userBorrows(@PathVariable Long id) {
        return borrowRepo.findByUser_Id(id);
    }

    @GetMapping("/borrows")
    public List<BorrowRecord> allBorrows() {
        return borrowRepo.findAll();
    }

    @PostMapping("/borrow/{userId}/{bookId}")
    public void borrowForUser(@PathVariable Long userId,
            @PathVariable Long bookId) {

        User user = userRepo.findById(userId).orElseThrow();
        Book book = bookRepo.findById(bookId).orElseThrow();

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));

        book.setAvailable(false);

        borrowRepo.save(record);
        bookRepo.save(book);
    }

    @PostMapping("/return/{borrowId}")
    public void forceReturn(@PathVariable Long borrowId) {

        BorrowRecord record = borrowRepo.findById(borrowId).orElseThrow();
        record.setReturnDate(LocalDate.now());
        record.getBook().setAvailable(true);

        borrowRepo.save(record);
    }

    @GetMapping("/books")
    public List<Book> listAllBooks() {
        return bookRepo.findAll();
    }

    @PostMapping("/addbook")
    public ResponseEntity<?> addBook(@RequestBody AddBookRequest request) {

        if (bookRepo.existsByBookPlace(request.getBookPlace())) {
            return ResponseEntity
                    .badRequest()
                    .body("Book with this BookPlace already exists");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setBookPlace(request.getBookPlace());
        book.setGenre(request.getGenre());
        book.setDescription(request.getDescription());
        book.setAvailable(true); // ✅ default

        bookRepo.save(book);

        return ResponseEntity.ok("Book added successfully");
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot delete book. It is currently borrowed.");
        }

        bookRepo.delete(book);

        return ResponseEntity.ok("Book deleted successfully");
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable Long bookId, @RequestBody UpdateBookRequest request) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (request.getTitle() != null)
            book.setTitle(request.getTitle());
        if (request.getAuthor() != null)
            book.setAuthor(request.getAuthor());
        if (request.getBookPlace() != null) {
            // Check uniqueness if changing
            if (!book.getBookPlace().equals(request.getBookPlace()) &&
                    bookRepo.existsByBookPlace(request.getBookPlace())) {
                return ResponseEntity.badRequest().body("BookPlace already exists");
            }
            book.setBookPlace(request.getBookPlace());
        }
        if (request.getGenre() != null)
            book.setGenre(request.getGenre());
        if (request.getDescription() != null)
            book.setDescription(request.getDescription());

        bookRepo.save(book);
        return ResponseEntity.ok("Book updated successfully");
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBookForUser(
            @RequestBody AdminBorrowRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            return ResponseEntity
                    .badRequest()
                    .body("Book is already borrowed");
        }

        // ✅ ROLE-BASED DUE DATE (INLINE)
        int borrowDays = 14; // default for normal users

        if (user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_STAFF"))) {
            borrowDays = 30;
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(borrowDays));

        book.setAvailable(false);

        borrowRepo.save(record);
        bookRepo.save(book);

        return ResponseEntity.ok(
                "Book issued successfully. Due in " + borrowDays + " days");
    }

    @PostMapping("/extend-due-date")
    public ResponseEntity<?> extendDueDate(
            @RequestBody ExtendDueDateRequest request) {

        BorrowRecord record = borrowRepo.findById(request.getBorrowId())
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (record.getReturnDate() != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Book already returned");
        }

        if (request.getExtraDays() <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Extra days must be greater than 0");
        }

        record.setDueDate(
                record.getDueDate().plusDays(request.getExtraDays()));

        borrowRepo.save(record);

        return ResponseEntity.ok(
                "Due date extended by " + request.getExtraDays() + " days");
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepo.save(user);

        return ResponseEntity.ok("User updated successfully");
    }

}
