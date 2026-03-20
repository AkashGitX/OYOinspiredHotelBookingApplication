package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.AuthResponse;
import com.akash.oyoclone.dto.LoginRequest;
import com.akash.oyoclone.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
