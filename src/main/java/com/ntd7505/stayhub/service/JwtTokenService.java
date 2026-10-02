package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.config.IssuedToken;
import com.ntd7505.stayhub.entity.User;

public interface JwtTokenService {
  IssuedToken generateToken(User user);
}
