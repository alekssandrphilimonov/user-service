package com.pioneer.scheduler;

import com.pioneer.model.Account;
import com.pioneer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceScheduler {

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 5000)
    public void increaseBalance() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance.add(BigDecimal.TEN);
            account.setBalance(newBalance);
            log.info("Increased balance for account {}: {} -> {}", account.getId(), oldBalance, newBalance);
        }
        accountRepository.saveAll(accounts);
    }
}