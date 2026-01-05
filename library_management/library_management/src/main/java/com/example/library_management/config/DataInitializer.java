package com.example.library_management.config;

import com.example.library_management.auth.entity.Book;
import com.example.library_management.auth.entity.Role;
import com.example.library_management.auth.entity.User;
import com.example.library_management.auth.repository.BookRepository;
import com.example.library_management.auth.repository.RoleRepository;
import com.example.library_management.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @PostConstruct
    public void initRoles() {

        if (roleRepository.findByName(ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, ROLE_USER));
        }

        if (roleRepository.findByName(ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, ROLE_ADMIN));
        }
        if (roleRepository.findByName("ROLE_STAFF").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_STAFF"));
        }
        if (roleRepository.findByName("ROLE_LIB_STAFF").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_LIB_STAFF"));
        }
        if (userRepository.findByUsername("staff").isEmpty()) {
            Role staffRole = roleRepository.findByName("ROLE_STAFF").orElseThrow();
            User staffUser = new User();
            staffUser.setUsername("staff");
            staffUser.setPassword(passwordEncoder.encode("password"));
            staffUser.setEmail("staff@library.com");
            staffUser.setRoles(java.util.Set.of(staffRole));
            userRepository.save(staffUser);
        }

        if (userRepository.findByUsername("libstaff").isEmpty()) {
            Role libStaffRole = roleRepository.findByName("ROLE_LIB_STAFF").orElseThrow();
            User libStaffUser = new User();
            libStaffUser.setUsername("libstaff");
            libStaffUser.setPassword(passwordEncoder.encode("password"));
            libStaffUser.setEmail("libstaff@library.com");
            libStaffUser.setRoles(java.util.Set.of(libStaffRole));
            userRepository.save(libStaffUser);
        }

        if (bookRepository.count() == 0) {

            Book book = new Book();
            book.setTitle("Clean Code");
            book.setAuthor("Robert C. Martin");
            book.setBookPlace("A-123");
            book.setGenre("Programming");
            book.setDescription("Best practices for writing clean code");
            book.setAvailable(true);

            bookRepository.save(book);
        }
    }

}
