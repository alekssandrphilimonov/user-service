package com.pioneer.controller;

import com.pioneer.dto.LoginRequest;
import com.pioneer.model.EmailData;
import com.pioneer.model.PhoneData;
import com.pioneer.repository.EmailDataRepository;
import com.pioneer.repository.PhoneDataRepository;
import com.pioneer.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailDataRepository emailRepository;
    private final PhoneDataRepository phoneRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login-email")
    public ResponseEntity<?> loginByEmail(@RequestBody LoginRequest request) {
        EmailData emailData = emailRepository.findByEmail(request.getLogin()).orElse(null);
        if (emailData == null || !passwordEncoder.matches(request.getPassword(), emailData.getUser().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token =  jwtUtil.generateToken(emailData.getUser().getId().toString());
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }

    @PostMapping("/login-phone")
    public ResponseEntity<?> loginByPhone(@RequestBody LoginRequest request) {
        PhoneData phoneData = phoneRepository.findByPhone(request.getLogin()).orElse(null);
        if (phoneData == null || !passwordEncoder.matches(request.getPassword(), phoneData.getUser().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token =  jwtUtil.generateToken(phoneData.getUser().getId().toString());
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
}
