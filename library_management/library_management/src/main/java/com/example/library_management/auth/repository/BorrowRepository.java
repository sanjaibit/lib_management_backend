package com.example.library_management.auth.repository;

import com.example.library_management.auth.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserUsernameAndReturnDateIsNull(String username);

    long countByUserUsername(String username);

    List<BorrowRecord> findByUser_Id(Long userId);

}
