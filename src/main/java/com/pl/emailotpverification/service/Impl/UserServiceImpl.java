package com.pl.emailotpverification.service.Impl;

import com.pl.emailotpverification.models.User;
import com.pl.emailotpverification.models.TempUser;
import com.pl.emailotpverification.repositories.UserRepo;
import com.pl.emailotpverification.request.RegisterRequest;
import com.pl.emailotpverification.response.RegisterResponse;
import com.pl.emailotpverification.service.UserService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, TempUser> tempUserStore = new ConcurrentHashMap<>();


    // @Override
    // public RegisterResponse register(RegisterRequest registerRequest) {
    //     User existingUser = userRepo.findByEmail(registerRequest.getEmail());
    //     if(existingUser!=null && existingUser.isVerified()){
    //         throw new RuntimeException("User already exists and verified");
    //     }
    //     User user = User.builder()
    //             .userId(UUID.randomUUID().toString())
    //             .userName(registerRequest.getUserName())
    //             .email(registerRequest.getEmail())
    //             .password(registerRequest.getPassword())
    //             .build();

        
    //     String otp = generateOtp();
    //     user.setOtp(otp);
    //     user.setOtpGeneratedTime(LocalDateTime.now());

    //     User savedUser = userRepo.save(user);

    //     // String otp = generateOtp();
    //     // user.setOtp(otp);

    //     sendVerificationEmail(savedUser.getEmail(), otp);

    //     RegisterResponse response = RegisterResponse.builder()
    //             .userName(user.getUserName())
    //             .email(user.getEmail())
    //             .build();

    //     return response;
    // }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        User existingUser = userRepo.findByEmail(registerRequest.getEmail());
        if(existingUser != null && existingUser.isVerified()){
            throw new RuntimeException("User already exists and verified");
        }

        // Create a temporary User Object
        TempUser tempUser = new TempUser(
            UUID.randomUUID().toString(),
            registerRequest.getUserName(),
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            generateOtp(),
            LocalDateTime.now()
        );

        //store the temporary user in the map
        tempUserStore.put(tempUser.getEmail(), tempUser);

        logger.info("Temporary User Stored "+tempUserStore);

        //send otp mail
        sendVerificationEmail(tempUser.getEmail(), tempUser.getOtp());

        RegisterResponse response = RegisterResponse.builder()
                .userName(tempUser.getUserName())
                .email(tempUser.getEmail())
                .build();

        return response;
    }


    private String generateOtp(){
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    private void sendVerificationEmail(String email, String otp){
        String subject = "Email Verification";
        String body = "Your verification otp is: "+otp;
        emailService.sendEmail(email, subject, body);
    }

    // @Override
    // public void verify(String email, String otp) {
    //     User user = userRepo.findByEmail(email);
    //     if(user == null){
    //         throw new RuntimeException("User not found");
    //     }else if(user.isVerified()){
    //         throw new RuntimeException("User is verified");
    //     }else if(user.getOtp()==null || !otp.equals(user.getOtp())){
    //         throw new RuntimeException("Invalid OTP");
    //     }else if(user.getOtpGeneratedTime().plusMinutes(2).isBefore(LocalDateTime.now())){
    //         throw new RuntimeException("OTP expired");
    //     }else{
    //         user.setVerified(true);
    //         userRepo.save(user);
    //     }
    // }


    @Override
    public void verify(String email, String otp) {
        TempUser tempUser = tempUserStore.get(email);

        if(tempUser == null){
            throw new RuntimeException("User Not Found");
        }else if(!otp.equals(tempUser.getOtp())){
            throw new RuntimeException("Invalid otp");
        }else if(tempUser.getOtpGeneratedTime().plusMinutes(2).isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP expired");
        }else{
            User user = User.builder()
                .userId(tempUser.getUserId())
                .userName(tempUser.getUserName())
                .email(tempUser.getEmail())
                .password(tempUser.getPassword())
                .isVerified(true)
                .otp(tempUser.getOtp())  // need to be remove for again and again verification
                .otpGeneratedTime(tempUser.getOtpGeneratedTime())  
                .build();
            
            userRepo.save(user);

            tempUserStore.remove(email); // remove the temporary stored user
        }

    }


    @Override
    public User login(String email, String password) {
        // TODO Auto-generated method stub

        User userByEmail = userRepo.findByEmail(email);

        if(userByEmail != null && userByEmail.isVerified() && userByEmail.getPassword().equals(password)){
            return userByEmail;
        }else{
            throw new RuntimeException("Internal Server Error");
        }

    }
}
