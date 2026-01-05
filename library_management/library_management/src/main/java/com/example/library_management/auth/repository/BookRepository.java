package com.example.library_management.auth.repository;

import com.example.library_management.auth.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    boolean existsByBookPlace(String bookPlace);

}
