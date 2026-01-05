package com.example.library_management.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private UserDetails userDetails;
    private BooksBorrowed booksBorrowed;
    private Fines fines;
    private AdditionalInfo additionalInfo;

    @Getter
    @Setter
    public static class UserDetails {
        private String userId;
        private String fullName;
        private String email;
        private String libraryCardNumber;
    }

    @Getter
    @Setter
    public static class BooksBorrowed {
        private List<BookInfo> currentlyBorrowed;
        private List<BookHistory> borrowingHistory;
        private int totalBooksBorrowed;
    }

    @Getter
    @Setter
    public static class BookInfo {
        private String title;
        private String author;
        private String bookPlace;
        private LocalDate checkoutDate;
        private LocalDate dueDate;
        private String returnStatus;
    }

    @Getter
    @Setter
    public static class BookHistory {
        private String title;
        private LocalDate checkoutDate;
        private LocalDate returnDate;
        private String status;
    }

    @Getter
    @Setter
    public static class Fines {
        private int outstandingAmount;
        private List<FineDetail> fineDetails;
        private List<Payment> paymentHistory;
        private int totalFinesPaid;
    }

    @Getter
    @Setter
    public static class FineDetail {
        private String book;
        private String reason;
        private LocalDate date;
        private int amount;
    }

    @Getter
    @Setter
    public static class Payment {
        private LocalDate date;
        private int amount;
        private String mode;
    }

    @Getter
    @Setter
    public static class AdditionalInfo {
        private List<String> reservedBooks;
        private int borrowingLimit;
        private int remainingQuota;
        private List<String> notificationPreferences;
        private LocalDate lastLibraryVisit;
        private List<String> favoriteGenres;
        private List<String> alerts;
    }
}
