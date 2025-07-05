package com.pioneer.controller;

import com.pioneer.dto.UserDto;
import com.pioneer.dto.UserFilter;
import com.pioneer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

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
    public ResponseEntity<String> getCurrentUser(HttpServletRequest request) {
        String login = (String) request.getAttribute("userLogin");
        return ResponseEntity.ok("Текущий пользователь: " + login);
    }
}
