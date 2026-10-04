package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.UserRegisterRequest;
import com.ntd7505.stayhub.dto.response.UserResponse;
import jakarta.validation.Valid;
import java.util.UUID;

public interface UserService {
  UserResponse register(@Valid UserRegisterRequest request);

  UserResponse getMyInfo(UUID userId);
}
