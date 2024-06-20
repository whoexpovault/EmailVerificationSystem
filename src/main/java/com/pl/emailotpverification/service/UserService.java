package com.pl.emailotpverification.service;

import com.pl.emailotpverification.models.User;
import com.pl.emailotpverification.request.RegisterRequest;
import com.pl.emailotpverification.response.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest registerRequest);

    void verify(String email, String otp);

    User login(String email, String password);
}
