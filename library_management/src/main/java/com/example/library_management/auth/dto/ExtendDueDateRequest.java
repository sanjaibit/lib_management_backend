package com.example.library_management.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendDueDateRequest {
    private Long borrowId;
    private int extraDays;
}
