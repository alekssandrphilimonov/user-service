package com.pioneer.service;

import com.pioneer.model.Account;
import com.pioneer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Cacheable("accounts")
    public Optional<Account> findByUserId(Long userId) {
        return accountRepository.findByUser_Id(userId);
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
