package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminUserController {

  private final UserService userService;

  //  @GetMapping("/users")
  //  public ResponseEntity<List<ApiResponse<UserResponse>>> getAllUsers() {}
}
