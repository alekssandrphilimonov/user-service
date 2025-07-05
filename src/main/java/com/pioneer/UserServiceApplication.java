package com.pioneer;

import com.pioneer.model.Account;
import com.pioneer.model.EmailData;
import com.pioneer.model.PhoneData;
import com.pioneer.model.User;
import com.pioneer.repository.AccountRepository;
import com.pioneer.repository.EmailDataRepository;
import com.pioneer.repository.PhoneDataRepository;
import com.pioneer.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class UserServiceApplication {

    @Bean
    public CommandLineRunner init(UserRepository userRepository,
                                  AccountRepository accountRepository,
                                  EmailDataRepository emailDataRepository,
                                  PhoneDataRepository phoneDataRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = User.builder()
                        .name("Test User")
                        .dateOfBirth(LocalDate.of(1993, 5, 1))
                        .password("password")
                        .build();
                userRepository.save(user);

                accountRepository.save(Account.builder()
                        .user(user)
                        .balance(BigDecimal.valueOf(100))
                        .build());

                emailDataRepository.save(EmailData.builder()
                        .user(user)
                        .email("test@example.com")
                        .build());

                phoneDataRepository.save(PhoneData.builder()
                        .user(user)
                        .phone("79207865432")
                        .build());

                System.out.println("Test user created!");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}