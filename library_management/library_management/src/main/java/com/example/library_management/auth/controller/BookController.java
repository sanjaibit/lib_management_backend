package com.example.library_management.auth.controller;

import com.example.library_management.auth.dto.AddBookRequest;
import com.example.library_management.auth.entity.Book;
import com.example.library_management.auth.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    // PUBLIC – explore books
    @GetMapping
    public List<Book> getAllBooks(
            @RequestParam(required = false) String search) {

        if (search != null) {
            return bookRepository
                    .findByTitleContainingIgnoreCase(search);
        }
        return bookRepository.findAll();
    }

    // PUBLIC – Explore

    // ADMIN – Add book
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book addBook(@RequestBody Book book) {
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    // ADMIN – Update book
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Book updateBook(@PathVariable Long id,
            @RequestBody Book updatedBook) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setGenre(updatedBook.getGenre());
        book.setDescription(updatedBook.getDescription());

        return bookRepository.save(book);
    }

    // ADMIN – Delete book
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }

}
