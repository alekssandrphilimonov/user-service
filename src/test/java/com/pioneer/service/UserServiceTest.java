package com.pioneer.service;

import com.pioneer.dto.UserDto;
import com.pioneer.dto.UserFilter;
import com.pioneer.model.Account;
import com.pioneer.model.EmailData;
import com.pioneer.model.PhoneData;
import com.pioneer.model.User;
import com.pioneer.repository.AccountRepository;
import com.pioneer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        accountService = mock(AccountService.class);
        userService = new UserService(userRepository, accountService);

        User user1 = User.builder()
                .id(1L)
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        EmailData email1 = new EmailData(1L, user1, "alice@mail.com");
        PhoneData phone1 = new PhoneData(1L, user1, "1111111111");

        user1.setEmails(List.of(email1));
        user1.setPhones(List.of(phone1));

        User user2 = User.builder()
                .id(2L)
                .name("Bob")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        EmailData email2 = new EmailData(2L, user2, "bob@mail.com");
        PhoneData phone2 = new PhoneData(2L, user2, "2222222222");

        user2.setEmails(List.of(email2));
        user2.setPhones(List.of(phone2));

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
    }

    @Test
    public void shouldFilterByEmail() {
        UserFilter filter = new UserFilter();
        filter.setEmail("bob@mail.com");

        Page<UserDto> result = userService.searchUsers(filter, 0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Bob");
    }

    @Test
    public void shouldReturnAllIfFilterEmpty() {
        UserFilter filter = new UserFilter();

        Page<UserDto> result = userService.searchUsers(filter, 0, 10);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void transfer_shouldMoveMoneyIfEnoughBalance() {
        Long fromUserId = 1L;
        Long toUserId = 2L;

        Account from = Account.builder()
                .id(1L)
                .balance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .build();

        Account to = Account.builder()
                .id(2L)
                .balance(new BigDecimal("50.00"))
                .initialBalance(new BigDecimal("50.00"))
                .build();

        when(accountService.findByUserId(fromUserId)).thenReturn(Optional.of(from));
        when(accountService.findByUserId(toUserId)).thenReturn(Optional.of(to));
        when(accountService.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        userService.transfer(fromUserId, toUserId, new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("80.00"), to.getBalance());
    }

    @Test
    void transfer_shouldFailIfNotEnoughMoney() {
        Long fromUserId = 1L;
        Long toUserId = 2L;

        Account from = Account.builder()
                .id(1L)
                .balance(new BigDecimal("10.00"))
                .initialBalance(new BigDecimal("10.00"))
                .build();

        Account to = Account.builder()
                .id(2L)
                .balance(new BigDecimal("20.00"))
                .initialBalance(new BigDecimal("20.00"))
                .build();

        when(accountService.findByUserId(fromUserId)).thenReturn(Optional.of(from));
        when(accountService.findByUserId(toUserId)).thenReturn(Optional.of(to));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.transfer(fromUserId, toUserId, new BigDecimal("30.00"))
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }
}
