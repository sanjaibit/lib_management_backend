package com.example.library_management.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {
    private String title;
    private String author;
    private String bookPlace;
    private String genre;
    private String description;
}
