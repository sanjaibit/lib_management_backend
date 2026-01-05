package com.example.library_management.auth.services;

import com.example.library_management.auth.dto.UserProfileResponse;
import com.example.library_management.auth.entity.BorrowRecord;
import com.example.library_management.auth.entity.Fine;
import com.example.library_management.auth.entity.User;
import com.example.library_management.auth.repository.BorrowRepository;
import com.example.library_management.auth.repository.FineRepository;
import com.example.library_management.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final FineRepository fineRepository;

    public UserProfileResponse buildUserProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = new UserProfileResponse();

        // ---------------- USER DETAILS ----------------
        UserProfileResponse.UserDetails details =
                new UserProfileResponse.UserDetails();

        details.setUserId("LIB-USR-" + user.getId());
        details.setFullName(user.getUsername()); // or first+last if available
        details.setEmail(user.getEmail());
        details.setLibraryCardNumber("LCN-" + user.getId());

        response.setUserDetails(details);

        // ---------------- BORROWED BOOKS ----------------
        List<BorrowRecord> activeBorrows =
                borrowRepository.findByUserUsernameAndReturnDateIsNull(username);

        List<UserProfileResponse.BookInfo> currentBooks =
                activeBorrows.stream().map(b -> {
                    UserProfileResponse.BookInfo info =
                            new UserProfileResponse.BookInfo();

                    info.setTitle(b.getBook().getTitle());
                    info.setAuthor(b.getBook().getAuthor());
                    info.setCheckoutDate(b.getBorrowDate());
                    info.setDueDate(b.getDueDate());
                    info.setReturnStatus("NOT_RETURNED");

                    return info;
                }).toList();

        UserProfileResponse.BooksBorrowed booksBorrowed =
                new UserProfileResponse.BooksBorrowed();

        booksBorrowed.setCurrentlyBorrowed(currentBooks);
        booksBorrowed.setTotalBooksBorrowed(
                (int) borrowRepository.countByUserUsername(username)
        );

        response.setBooksBorrowed(booksBorrowed);

        // ---------------- FINES ----------------
        List<Fine> fines = fineRepository.findUnpaidFinesByUsername(username);

        int outstandingAmount =
                fines.stream().mapToInt(Fine::getAmount).sum();

        List<UserProfileResponse.FineDetail> fineDetails =
                fines.stream().map(f -> {
                    UserProfileResponse.FineDetail fd =
                            new UserProfileResponse.FineDetail();

                    fd.setBook(f.getBorrowRecord()
                            .getBook().getTitle());
                    fd.setReason(f.getReason());
                    fd.setDate(f.getCreatedDate());
                    fd.setAmount(f.getAmount());

                    return fd;
                }).toList();

        UserProfileResponse.Fines finesBlock =
                new UserProfileResponse.Fines();

        finesBlock.setOutstandingAmount(outstandingAmount);
        finesBlock.setFineDetails(fineDetails);
        finesBlock.setTotalFinesPaid(
                fineRepository.totalPaidFinesByUsername(username)
        );

        response.setFines(finesBlock);

        return response;
    }
}
