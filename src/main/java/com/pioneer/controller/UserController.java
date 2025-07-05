package com.pioneer.controller;

import com.pioneer.dto.TransferRequest;
import com.pioneer.dto.UserDto;
import com.pioneer.dto.UserFilter;
import com.pioneer.model.User;
import com.pioneer.security.JwtUtil;
import com.pioneer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Поиск пользователей", security = @SecurityRequirement(name = "BearerAuth"))
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public Page<UserDto> search(
            @Valid @ModelAttribute UserFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/users/search called with filter={}, page={}, size={}", filter, page, size);
        return userService.searchUsers(filter, page, size);
    }

    @Operation(summary = "Тестовый endpoint (авторизация требуется)", security = @SecurityRequirement(name = "BearerAuth"))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    public String getAllUsers() {
        return "Access granted to protected resource!";
    }

    @Operation(summary = "Получить текущего пользователя", security = @SecurityRequirement(name = "BearerAuth"))
    @SecurityRequirement(name = "BearerAuth")
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

    @Operation(summary = "Трансфер денег", security = @SecurityRequirement(name = "BearerAuth"))
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody @Valid TransferRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long fromUserId = Long.parseLong(jwtUtil.extractUserId(token));

        userService.transfer(fromUserId, request.getToUserId(), request.getValue());
        return ResponseEntity.ok("Transfer successful");
    }
}
