package com.bank.auth_service.controller;

import com.bank.auth_service.dto.RegisterRequest;
import com.bank.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> customerOnly() {
        return ResponseEntity.ok("Welcome Customer!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Welcome Admin!");
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(
             @Valid @RequestBody RegisterRequest request
            ){
        return ResponseEntity.ok(
                authService.register(request)
        );
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String username,
            @RequestParam String password) {

        return ResponseEntity.ok(
                authService.login(username, password)
        );
    }
    @GetMapping("/profile")
    public ResponseEntity<String> profile(Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                "Welcome " + username + ", you are authenticated!"
        );
    }
}
