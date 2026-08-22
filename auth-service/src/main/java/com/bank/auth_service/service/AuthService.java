package com.bank.auth_service.service;

import com.bank.auth_service.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);
    String login(String username,String password);
}
