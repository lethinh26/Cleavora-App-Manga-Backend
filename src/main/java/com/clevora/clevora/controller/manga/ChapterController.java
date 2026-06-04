package com.clevora.clevora.controller.manga;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.manga.ChapterDetailResponse;
import com.clevora.clevora.dto.manga.ChapterRequest;
import com.clevora.clevora.dto.manga.ChapterResponse;
import com.clevora.clevora.service.manga.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("/mangas/{mangaId}/chapters")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByMangaId(
            @PathVariable Integer mangaId) {
        List<ChapterResponse> chapters = chapterService.getChaptersByMangaId(mangaId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chapter thành công", chapters));
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<ChapterDetailResponse>> getChapterDetail(
            @PathVariable Integer chapterId) {
        ChapterDetailResponse chapter = chapterService.getChapterDetail(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết chapter thành công", chapter));
    }

    @PostMapping("/admin/mangas/{mangaId}/chapters")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer mangaId,
            @Valid @RequestBody ChapterRequest request) {
        ChapterResponse response = chapterService.createChapter(userDetails.getUsername(), mangaId, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm chapter thành công", response));
    }

    @PutMapping("/admin/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer chapterId,
            @Valid @RequestBody ChapterRequest request) {
        ChapterResponse response = chapterService.updateChapter(userDetails.getUsername(), chapterId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật chapter thành công", response));
    }

    @DeleteMapping("/admin/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<String>> deleteChapter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer chapterId) {
        chapterService.deleteChapter(userDetails.getUsername(), chapterId);
        return ResponseEntity.ok(ApiResponse.success("Xóa chapter thành công"));
    }

    @PostMapping("/chapters/{chapterId}/view")
    public ResponseEntity<ApiResponse<String>> incrementViewCount(
            @PathVariable Integer chapterId) {
        chapterService.incrementViewCount(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Tăng lượt xem thành công"));
    }
}
