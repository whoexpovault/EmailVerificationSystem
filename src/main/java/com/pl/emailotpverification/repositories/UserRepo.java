package com.pl.emailotpverification.repositories;

import com.pl.emailotpverification.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
    User findByEmail(String email);
}
