package com.pioneer;

import com.pioneer.model.Account;
import com.pioneer.model.EmailData;
import com.pioneer.model.PhoneData;
import com.pioneer.model.User;
import com.pioneer.repository.AccountRepository;
import com.pioneer.repository.EmailDataRepository;
import com.pioneer.repository.PhoneDataRepository;
import com.pioneer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class UserServiceApplication {

    @Bean
    public CommandLineRunner init(UserRepository userRepository,
                                  AccountRepository accountRepository,
                                  EmailDataRepository emailDataRepository,
                                  PhoneDataRepository phoneDataRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = User.builder()
                        .name("Test User")
                        .dateOfBirth(LocalDate.of(1993, 5, 1))
                        .password(passwordEncoder.encode("password"))
                        .build();
                userRepository.save(user);

                Account account = Account.builder()
                        .user(user)
                        .balance(BigDecimal.valueOf(100))
                        .build();
                account.setInitialBalance(account.getBalance());
                accountRepository.save(account);

                emailDataRepository.save(EmailData.builder()
                        .user(user)
                        .email("test@mail.com")
                        .build());

                phoneDataRepository.save(PhoneData.builder()
                        .user(user)
                        .phone("79207865432")
                        .build());

                log.info("Test user created!");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}