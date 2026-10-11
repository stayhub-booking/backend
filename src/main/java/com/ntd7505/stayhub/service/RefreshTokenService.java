package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.entity.User;

public interface RefreshTokenService {

  String generateToken(User user);

  Rotation rotate(String rawToken);

  record Rotation(User user, String refreshToken) {}
}
