package com.ntd7505.stayhub.dto.response;

public record AuthenticationResponse(
    String accessToken, String tokenType, long expiresIn, UserResponse user, String refreshToken) {}
