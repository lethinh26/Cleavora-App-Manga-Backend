package com.clevora.clevora.controller.manga;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.manga.MangaRequest;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.service.manga.MangaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
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

    @PutMapping("/admin/manga/{id}")
    public ResponseEntity<ApiResponse<MangaResponse>> updateManga(Authentication authentication,
                                                                  @Valid @RequestBody MangaRequest mangaRequest,
                                                                  @PathVariable int id){
        MangaResponse mangaResponse = mangaService.updateManga(authentication.getName(), id ,mangaRequest);
        return ResponseEntity.ok(ApiResponse.success("Sửa mẫu truyện thành công", mangaResponse));
    }

    @DeleteMapping("/admin/manga/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteManga(Authentication authentication,
                                                           @Valid @RequestBody MangaRequest mangaRequest,
                                                           @PathVariable int id){
        mangaService.deleteManga(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mẫu truyện thành công"));
    }

}
