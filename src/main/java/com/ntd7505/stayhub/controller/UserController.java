package com.ntd7505.stayhub.controller;

import com.ntd7505.stayhub.dto.request.ChangePasswordRequest;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/me/password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    userService.changePassword(request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.PASSWORD_CHANGED, null));
  }
}
