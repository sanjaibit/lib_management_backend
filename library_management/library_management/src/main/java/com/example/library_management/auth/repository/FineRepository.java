package com.example.library_management.auth.repository;

import com.example.library_management.auth.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    @Query("""
    SELECT f FROM Fine f
    WHERE f.borrowRecord.user.username = :username
    AND f.paid = false
    """)
    List<Fine> findUnpaidFinesByUsername(@Param("username") String username);

    @Query("""
    SELECT COALESCE(SUM(f.amount), 0)
    FROM Fine f
    WHERE f.borrowRecord.user.username = :username
    AND f.paid = true
    """)
    int totalPaidFinesByUsername(@Param("username") String username);
}
