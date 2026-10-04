package com.ntd7505.stayhub.controller;

import com.ntd7505.stayhub.dto.request.LoginRequest;
import com.ntd7505.stayhub.dto.request.RefreshTokenRequest;
import com.ntd7505.stayhub.dto.request.UserRegisterRequest;
import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.dto.response.AuthenticationResponse;
import com.ntd7505.stayhub.dto.response.UserResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AuthService;
import com.ntd7505.stayhub.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
@SecurityRequirements
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponse>> register(
      @Valid @RequestBody UserRegisterRequest request) {
    var result = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(ResponseCode.USER_CREATED, result));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
      @Valid @RequestBody LoginRequest request) {

    var result = authService.login(request);

    return ResponseEntity.ok(ApiResponse.success(ResponseCode.LOGIN_SUCCESS, result));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {

    var result = authService.refresh(request);

    return ResponseEntity.ok()
        .header("Cache-Control", "no-store")
        .body(ApiResponse.success(ResponseCode.SUCCESS, result));
  }
}
