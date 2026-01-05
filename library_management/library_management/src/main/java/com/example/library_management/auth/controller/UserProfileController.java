package com.example.library_management.auth.controller;

import com.example.library_management.auth.dto.UserProfileResponse;
import com.example.library_management.auth.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;


    @GetMapping
    public UserProfileResponse getProfile(Authentication auth) {

        if (auth == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "JWT expired or invalid. Please login again."
            );
        }

        String username = auth.getName(); // from JWT
        return userProfileService.buildUserProfile(username);
    }
}
