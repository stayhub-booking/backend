package com.ntd7505.stayhub.controller.client;

import com.ntd7505.stayhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientUserController {

  UserService userService;

  //    @GetMapping("/me")
  //    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
  ////        var rs = userService.getMyInfo();
  //    }

}
