package com.pioneer.controller;

import com.pioneer.dto.UserDto;
import com.pioneer.dto.UserFilter;
import com.pioneer.model.User;
import com.pioneer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public Page<UserDto> search(
            UserFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/users/search called with filter={}, page={}, size={}", filter, page, size);
        return userService.searchUsers(filter, page, size);
    }

    @GetMapping
    public String getAllUsers() {
        return "Access granted to protected resource!";
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userLogin");

        User user = userService.findById(Long.parseLong(userId));
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(user.getEmails().stream().map(e -> e.getEmail()).collect(Collectors.toList()))
                .phones(user.getPhones().stream().map(p -> p.getPhone()).collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(dto);
    }

}
