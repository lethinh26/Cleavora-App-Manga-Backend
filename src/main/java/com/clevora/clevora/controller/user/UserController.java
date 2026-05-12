package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.UserProfileRequest;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponse profile = userService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(Authentication authentication,
                                                                   @RequestBody UserProfileRequest request){
        UserResponse profile = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Sửa thành công thông tin"));
    }
}
