package com.pl.emailotpverification.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TempUser {
    private String userId;
    private String userName;
    private String email;
    private String password;
    private String otp;
    private LocalDateTime otpGeneratedTime;
}
