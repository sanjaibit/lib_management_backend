package com.example.library_management.auth.controller;

import com.example.library_management.auth.entity.Book;
import com.example.library_management.auth.entity.BorrowRecord;
import com.example.library_management.auth.entity.User;
import com.example.library_management.auth.repository.BookRepository;
import com.example.library_management.auth.repository.BorrowRepository;
import com.example.library_management.auth.repository.UserRepository;
import com.example.library_management.auth.services.FineService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowRepository borrowRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    @PostMapping("/{bookId}")
    public ResponseEntity<?> borrowBook(
            @PathVariable Long bookId,
            Authentication authentication) {

        String username = authentication.getName();

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            return ResponseEntity.badRequest().body("Book not available");
        }

        User user = userRepo.findByUsername(username).get();

        BorrowRecord record = new BorrowRecord(
                null,
                user,
                book,
                LocalDate.now(),
                LocalDate.now().plusDays(14), // due date
                null,
                null
        );


        book.setAvailable(false);

        borrowRepo.save(record);
        bookRepo.save(book);

        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<?> returnBook(
            @PathVariable Long bookId,
            Authentication authentication) {

        BorrowRecord record = borrowRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (record.getReturnDate() != null) {
            return ResponseEntity.badRequest().body("Book already returned");
        }

        record.setReturnDate(LocalDate.now());
        record.getBook().setAvailable(true);

        borrowRepo.save(record);
        bookRepo.save(record.getBook());

        return ResponseEntity.ok("Book returned successfully");
    }

    @GetMapping("/my/fines")
    public List<Map<String, Object>> myFines(
            Authentication auth) {

        String username = auth.getName();

        List<BorrowRecord> records =
                borrowRepo.findByUserUsernameAndReturnDateIsNull(username);

        return records.stream().map(record -> {
            FineService fineService = new FineService();
            int fine = fineService.calculateFine(record);

            Map<String, Object> map = new HashMap<>();
            map.put("book", record.getBook().getTitle());
            map.put("dueDate", record.getDueDate());
            map.put("fine", fine);

            return map;
        }).toList();
    }

    @GetMapping("/my")
    public List<BorrowRecord> myBorrowedBooks(Authentication auth) {
        return borrowRepo
                .findByUserUsernameAndReturnDateIsNull(auth.getName());
    }


}
