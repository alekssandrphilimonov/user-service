package com.pioneer.repository;

import com.pioneer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmails_Email(String email);

    Optional<User> findByPhones_Phone(String phone);
}
