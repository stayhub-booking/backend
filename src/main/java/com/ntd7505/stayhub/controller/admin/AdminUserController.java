package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.dto.response.UserResponse;
import com.ntd7505.stayhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<ApiResponse<UserResponse>>> getAllUsers() {

    }

}
