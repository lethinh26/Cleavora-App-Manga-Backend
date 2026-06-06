package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.*;
import com.clevora.clevora.entity.ReadingHistory;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.service.manga.ReadingHistoryService;
import com.clevora.clevora.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReadingHistoryService readingHistoryService;

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
    public ResponseEntity<ApiResponse<ReadingHistoryResponse>> updateLocationHistory(Authentication authentication, @RequestBody ReadingHistoryRequest request) {
        // Lưu/cập nhật vị trí đọc (UPSERT). Nhận manga_id, chapter_id, last_page. Tự động cập nhật last_read_at.

        return ResponseEntity.ok(ApiResponse.success("Cập nhập thành công",
                readingHistoryService.upsertReadingHistory(authentication.getName(),
                        request.getMangaId(), request.getChapterId(), request.getPage())));
    }

    @GetMapping("/history/{mangaId}") // 27
    public ResponseEntity<ApiResponse<ReadingHistoryResponse>> getHistoryManga(Authentication authentication, @PathVariable Integer mangaId) {
        // Lấy vị trí đọc gần nhất của 1 truyện. Dùng cho nút "Tiếp tục đọc". Trả về chapter_id + last_page.
        ReadingHistory history = readingHistoryService.getReadingHistoryByManga(authentication.getName(), mangaId);
        if (history == null) {
            throw new ResourceNotFoundException("Chưa có lịch sử đọc");
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu thành công", ReadingHistoryResponse.buildingFromEntity(history)));
    }

    @GetMapping("/history") // 24
    public ResponseEntity<ApiResponse<List<ReadingHistoryResponse>>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu thành công", readingHistoryService.getReadingHistories(authentication.getName())));
    }

    @DeleteMapping("/history/{mangaId}") // 25
    public ResponseEntity<ApiResponse<Void>> deleteHistoryManga(Authentication authentication, @PathVariable Integer mangaId) {
        readingHistoryService.deleteReadingHistoryByManga(authentication.getName(), mangaId);
        return ResponseEntity.ok(ApiResponse.success("Xóa dữ liệu manga thành công"));
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> deleteHistory(Authentication authentication){
        readingHistoryService.deleteAllReadingHistory(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Xóa dữ liệu lịch sử thành công"));
    }

}
