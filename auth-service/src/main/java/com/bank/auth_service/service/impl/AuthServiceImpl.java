package com.bank.auth_service.service.impl;

import com.bank.auth_service.dto.RegisterRequest;
import com.bank.auth_service.entity.AuthUser;
import com.bank.auth_service.repository.AuthUserRepository;
import com.bank.auth_service.service.AuthService;
import com.bank.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(RegisterRequest request) {
        //1.check the user is existing or not
        if(authUserRepository.findByUsername(request.getUsername())
                .isPresent()){
            throw new RuntimeException("User already exists");
        }
        //2.register a new user
        AuthUser authUser=AuthUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
        authUserRepository.save(authUser);
        return "User registerd successfully";
    }

    @Override
    public String login(String username, String password) {
        AuthUser user = authUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return jwtService.generateToken(user.getUsername(),user.getRole());
    }
}
