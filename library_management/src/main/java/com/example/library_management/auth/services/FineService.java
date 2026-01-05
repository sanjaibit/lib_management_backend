package com.example.library_management.auth.services;

import com.example.library_management.auth.entity.BorrowRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class FineService {

    private static final int FINE_PER_DAY = 5;

    public int calculateFine(BorrowRecord record) {

        if (record.getReturnDate() != null) return 0;

        LocalDate today = LocalDate.now();

        if (today.isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(
                    record.getDueDate(), today);
            return (int) daysLate * FINE_PER_DAY;
        }

        return 0;
    }
}

