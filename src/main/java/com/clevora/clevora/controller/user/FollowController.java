package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.FollowListResponse;
import com.clevora.clevora.dto.user.FollowResponse;
import com.clevora.clevora.dto.user.FollowStatusResponse;
import com.clevora.clevora.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/v1/mangas/{id}/follow")
    public ResponseEntity<ApiResponse<FollowResponse>> toggleFollow(
            Authentication authentication,
            @PathVariable("id") Integer mangaId) {
        FollowResponse result = followService.toggleFollow(authentication.getName(), mangaId);
        String message = result.isFollowed() ? "Đã theo dõi truyện" : "Đã bỏ theo dõi truyện";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    @GetMapping("/v1/mangas/{id}/follow-status")
    public ResponseEntity<ApiResponse<FollowStatusResponse>> getFollowStatus(
            Authentication authentication,
            @PathVariable("id") Integer mangaId) {
        FollowStatusResponse result = followService.getFollowStatus(authentication.getName(), mangaId);
        return ResponseEntity.ok(ApiResponse.success("Trạng thái theo dõi", result));
    }

    @GetMapping("/v1/me/follows")
    public ResponseEntity<ApiResponse<FollowListResponse>> getFollows(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        FollowListResponse result = followService.getFollows(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Danh sách truyện đang theo dõi", result));
    }
}
