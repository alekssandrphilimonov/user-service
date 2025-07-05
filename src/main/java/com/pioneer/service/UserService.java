package com.pioneer.service;

import com.pioneer.dto.UserDto;
import com.pioneer.dto.UserFilter;
import com.pioneer.model.Account;
import com.pioneer.model.User;
import com.pioneer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;

    public Page<UserDto> searchUsers(UserFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<User> allUsers = userRepository.findAll();

        List<User> filtered = allUsers.stream()
                .filter(user -> filter.getName() == null || user.getName() != null && user.getName().startsWith(filter.getName()))
                .filter(user -> filter.getDateOfBirth() == null || user.getDateOfBirth() != null && user.getDateOfBirth().isAfter(filter.getDateOfBirth()))
                .filter(user -> filter.getEmail() == null || user.getEmails().stream().anyMatch(e -> e.getEmail().equals(filter.getEmail())))
                .filter(user -> filter.getPhone() == null || user.getPhones().stream().anyMatch(p -> p.getPhone().equals(filter.getPhone())))
                .collect(Collectors.toList());

        List<UserDto> content = filtered.stream()
                .skip((long) page * size)
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .dateOfBirth(user.getDateOfBirth())
                        .emails(user.getEmails().stream().map(e -> e.getEmail()).collect(Collectors.toList()))
                        .phones(user.getPhones().stream().map(p -> p.getPhone()).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, filtered.size());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountService.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountService.findByUserId(toUserId)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        if (fromAccount.getBalance().compareTo(value) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(value));
        toAccount.setBalance(toAccount.getBalance().add(value));

        accountService.save(fromAccount);
        accountService.save(toAccount);
    }
}
