package com.example.library_management.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "fines")
@Getter
@Setter
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "borrow_id")
    private BorrowRecord borrowRecord;

    private int amount;
    private String reason;
    private LocalDate createdDate;
    private boolean paid;
}
