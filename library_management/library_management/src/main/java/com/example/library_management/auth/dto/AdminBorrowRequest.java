package com.example.library_management.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBorrowRequest {
    private String username;
    private Long bookId;
}
