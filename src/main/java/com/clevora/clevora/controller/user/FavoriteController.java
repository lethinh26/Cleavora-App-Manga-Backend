package com.clevora.clevora.controller.user;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.user.FavoriteListResponse;
import com.clevora.clevora.dto.user.LikeResponse;
import com.clevora.clevora.dto.user.LikeStatusResponse;
import com.clevora.clevora.service.user.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/v1/mangas/{id}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
            Authentication authentication,
            @PathVariable("id") Integer mangaId) {
        LikeResponse result = favoriteService.toggleLike(authentication.getName(), mangaId);
        String message = result.isLiked() ? "Đã thích truyện" : "Đã bỏ thích truyện";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    @GetMapping("/v1/mangas/{id}/like-status")
    public ResponseEntity<ApiResponse<LikeStatusResponse>> getLikeStatus(
            Authentication authentication,
            @PathVariable("id") Integer mangaId) {
        LikeStatusResponse result = favoriteService.getLikeStatus(authentication.getName(), mangaId);
        return ResponseEntity.ok(ApiResponse.success("Trạng thái like", result));
    }

    @GetMapping("/v1/me/favorites")
    public ResponseEntity<ApiResponse<FavoriteListResponse>> getFavorites(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        FavoriteListResponse result = favoriteService.getFavorites(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Danh sách truyện đã thích", result));
    }
}
