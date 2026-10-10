package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.LoginRequest;
import com.ntd7505.stayhub.dto.request.RefreshTokenRequest;
import com.ntd7505.stayhub.dto.response.auth.AuthenticationResponse;

public interface AuthService {

  AuthenticationResponse login(LoginRequest request);

  AuthenticationResponse refresh(RefreshTokenRequest request);
}
