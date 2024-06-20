package com.pl.emailotpverification.models;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    private String userId;

    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String otp;
    
    private boolean isVerified;

    private LocalDateTime otpGeneratedTime;

}
