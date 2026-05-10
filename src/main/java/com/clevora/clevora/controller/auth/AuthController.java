package com.clevora.clevora.controller.auth;

import com.clevora.clevora.dto.auth.LoginRequest;
import com.clevora.clevora.dto.auth.LoginResponse;
import com.clevora.clevora.dto.auth.RegisterRequest;
import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.service.auth.AuthService;
import com.clevora.clevora.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", loginResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        String email = authentication.getName();
        UserResponse user = userService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.success("Token hợp lệ", user));
    }
}
