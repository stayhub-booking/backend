package com.ntd7505.stayhub.controller;

import com.ntd7505.stayhub.dto.request.UserRegisterRequest;
import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.dto.response.UserResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponse>> register(
      @Valid @RequestBody UserRegisterRequest request) {
    var result = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(ResponseCode.USER_CREATED, result));
  }
}
