package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.dto.manga.RejectRequest;
import com.clevora.clevora.dto.user.ChangeRoleRequest;
import com.clevora.clevora.dto.user.DashboardStatsResponse;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.service.user.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ============ DASHBOARD (#45) ============

    @GetMapping("/v1/admin/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê thành công", stats));
    }

    // ============ USER MANAGEMENT (#43, #44) ============

    @GetMapping("/v1/admin/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    @PutMapping("/v1/admin/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserActive(
            Authentication authentication,
            @PathVariable Integer id) {
        UserResponse user = adminService.toggleUserActive(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái tài khoản thành công", user));
    }

    // ============ MANGA APPROVAL (#34, #35, #36) ============

    @GetMapping("/v1/admin/mangas/pending")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getPendingMangas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MangaResponse> mangas = adminService.getPendingMangas(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách truyện chờ duyệt thành công", mangas));
    }

    @PutMapping("/v1/admin/mangas/{id}/approve")
    public ResponseEntity<ApiResponse<MangaResponse>> approveManga(
            Authentication authentication,
            @PathVariable Integer id) {
        MangaResponse manga = adminService.approveManga(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Duyệt truyện thành công", manga));
    }

    @PutMapping("/v1/admin/mangas/{id}/reject")
    public ResponseEntity<ApiResponse<MangaResponse>> rejectManga(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody RejectRequest request) {
        MangaResponse manga = adminService.rejectManga(authentication.getName(), id, request.getRejectReason());
        return ResponseEntity.ok(ApiResponse.success("Từ chối truyện thành công", manga));
    }

    // ============ SUPERADMIN - CHANGE ROLE (#46) ============

    @PutMapping("/v1/superadmin/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UserResponse user = adminService.changeUserRole(authentication.getName(), id, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("Thay đổi role thành công", user));
    }

    // ============ MANGA BAN/UNBAN ============

    @PutMapping("/v1/admin/mangas/{id}/ban")
    public ResponseEntity<ApiResponse<MangaResponse>> banManga(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody RejectRequest request) {
        MangaResponse manga = adminService.banManga(authentication.getName(), id, request.getRejectReason());
        return ResponseEntity.ok(ApiResponse.success("Đã cấm truyện", manga));
    }

    @PutMapping("/v1/admin/mangas/{id}/unban")
    public ResponseEntity<ApiResponse<MangaResponse>> unbanManga(
            Authentication authentication,
            @PathVariable Integer id) {
        MangaResponse manga = adminService.unbanManga(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã bỏ cấm truyện", manga));
    }
}
