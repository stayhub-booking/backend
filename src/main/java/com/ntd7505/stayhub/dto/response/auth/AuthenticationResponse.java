package com.ntd7505.stayhub.dto.response.auth;

import com.ntd7505.stayhub.dto.response.user.UserResponse;

public record AuthenticationResponse(
    String accessToken, String tokenType, long expiresIn, UserResponse user, String refreshToken) {}
