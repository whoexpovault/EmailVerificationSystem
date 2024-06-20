package com.pl.emailotpverification.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pl.emailotpverification.models.User;
import com.pl.emailotpverification.request.RegisterRequest;
import com.pl.emailotpverification.response.RegisterResponse;
import com.pl.emailotpverification.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest){
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String otp){
        try {
            userService.verify(email, otp);
            return new ResponseEntity<>("User verified successfully", HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password){
        User user = userService.login(email, password);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
