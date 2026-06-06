package com.clevora.clevora.controller.manga;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.manga.MangaRequest;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.service.manga.MangaService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class MangaController {
    private MangaService mangaService;
    @PostMapping("/admin/mangas")
    public ResponseEntity<ApiResponse<MangaResponse>> createManga(Authentication authentication,
                                                                  @Valid @RequestBody MangaRequest mangaRequest){
        MangaResponse mangaResponse = mangaService.createManga(authentication.getName(), mangaRequest);
        return ResponseEntity.ok(ApiResponse.success("Tạo mới truyện thành công", mangaResponse));
    }

    @PutMapping("/admin/mangas/{id}")
    public ResponseEntity<ApiResponse<MangaResponse>> updateManga(Authentication authentication,
                                                                  @Valid @RequestBody MangaRequest mangaRequest,
                                                                  @PathVariable int id){
        MangaResponse mangaResponse = mangaService.updateManga(authentication.getName(), id ,mangaRequest);
        return ResponseEntity.ok(ApiResponse.success("Sửa mẫu truyện thành công", mangaResponse));
    }

    @GetMapping("/admin/mangas/{id}")
    public ResponseEntity<ApiResponse<MangaResponse>> getMangaById(@PathVariable int id){
        MangaResponse response = mangaService.getMangaById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy truyện thành công", response));
    }

    // Public: lấy chi tiết truyện theo id (dùng cho Favorites/Following/History navigation)
    @GetMapping("/mangas/by-id/{id}")
    public ResponseEntity<ApiResponse<MangaResponse>> getPublicMangaById(@PathVariable int id){
        MangaResponse response = mangaService.getMangaById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy truyện thành công", response));
    }

    @DeleteMapping("/admin/mangas/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteManga(Authentication authentication,
                                                           @PathVariable int id){
        mangaService.deleteManga(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mẫu truyện thành công"));
    }

//    8: v1/mangas: Danh sách truyện đã duyệt (APPROVED). Hỗ trợ phân trang, sắp xếp (mới nhất, lượt xem, like, follow), lọc theo trạng thái.
    @GetMapping("/mangas")
    public ResponseEntity<ApiResponse<List<MangaResponse>>> getMangas(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size,
                                                                      @RequestParam(defaultValue = "newest") String sortBy,
                                                                      @RequestParam(required = false) String status){
        List<MangaResponse> mangaResponses = mangaService.getApprovedMangas(page, size, sortBy, status);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách truyện thành công", mangaResponses));
    }
//    9: /v1/mangas/{slug}: Chi tiết truyện theo slug. Trả về thông tin truyện, danh sách thể loại, thống kê (view, like, follow).
    @GetMapping("/mangas/{slug}")
    public ResponseEntity<ApiResponse<MangaResponse>> getMangaDetail(@PathVariable String slug) {
        MangaResponse response = mangaService.getMangaDetailBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Lấy truyện thành công",response));
    }
//    11: /v1/mangas/search: Tìm kiếm truyện theo keyword (title, author_name). Chỉ trả về truyện APPROVED. Hỗ trợ phân trang.
    @GetMapping("/mangas/search")
    public ResponseEntity<Page<MangaResponse>> searchMangas(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<MangaResponse> response = mangaService.searchMangas(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 28 post: /v1/mangas/submit: User submit truyện mới.
    // Nhận title, author_name, description, cover_image, genres.
    // Tạo manga với approval_status = PENDING, submitted_by = user hiện tại.
    @PostMapping("/mangas/submit")
    public ResponseEntity<ApiResponse<MangaResponse>> submitManga(
            Authentication authentication,
            @Valid @RequestBody MangaRequest request) {

        String currentUserEmail = authentication.getName();
        MangaResponse response = mangaService.submitManga(request, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.success("Submit thành công mẫu truyễn", response));
    }

    // 29: /v1/me/mangas: Danh sách truyện user đã đăng. Hỗ trợ lọc theo approval_status (PENDING/APPROVED/REJECTED). Hiển thị reject_reason nếu bị từ chối.
    @GetMapping("/me/mangas")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getMyMangas(
            @RequestParam(required = false, name = "approval_status") String approvalStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Page<MangaResponse> response = mangaService.getMyMangas(authentication.getName(), approvalStatus, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sach manga của tôi thành công",response));
    }

    // Admin: danh sách tất cả truyện (optional filter by approval_status)
    @GetMapping("/admin/mangas")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getAdminMangas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "approval_status") String approvalStatus) {
        Page<MangaResponse> response = mangaService.getAdminMangas(page, size, approvalStatus);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách truyện thành công", response));
    }

    // 30 put: /v1/me/mangas/{id}: User chỉnh sửa truyện đã đăng (chỉ khi PENDING hoặc REJECTED). Cho phép sửa title, description, cover, genres rồi re-submit.
    @PutMapping("/me/mangas/{id}")
    public ResponseEntity<ApiResponse<MangaResponse>> updateMyManga(
            @PathVariable int id,
            @Valid @RequestBody MangaRequest request,
            Authentication authentication) {

        MangaResponse response = mangaService.updateMyManga(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật truyện thành công", response));
    }

}
