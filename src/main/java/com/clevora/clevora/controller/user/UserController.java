package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.UserProfileRequest;
import com.clevora.clevora.dto.user.ChangePassRequest;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.service.user.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileRequest request) {
        String email = authentication.getName();
        UserResponse profile = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", profile));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(Authentication authentication, @Valid @RequestBody ChangePassRequest request) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công"));
    }
    
    @PutMapping("/history") // 23
    public ResponseEntity<ApiResponse<Void>> updateLocationHistory(Authentication authentication) {
        // Lưu/cập nhật vị trí đọc (UPSERT). Nhận manga_id, chapter_id, last_page. Tự động cập nhật last_read_at.

        return ResponseEntity.ok(ApiResponse.success("Cập nhập thành công"));
    }

    @GetMapping("/history/{mangaId}") // 27
    public ResponseEntity<ApiResponse<Void>> getHistoryManga(Authentication authentication, @PathVariable String mangaId) {
        // Lấy vị trí đọc gần nhất của 1 truyện. Dùng cho nút "Tiếp tục đọc". Trả về chapter_id + last_page.

        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu thành công"));
    }

}
