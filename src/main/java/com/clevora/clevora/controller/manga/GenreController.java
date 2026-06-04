package com.clevora.clevora.controller.manga;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.manga.GenreRequest;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.entity.Genre;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.service.manga.GenreService;
import com.clevora.clevora.service.manga.MangaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class GenreController {
    private GenreService genreService;
    private MangaService mangaService;

    @PostMapping("/admin/genres")
    public ResponseEntity<ApiResponse<Genre>> createGenre(Authentication authentication,  @Valid @RequestBody GenreRequest genreRequest){
        String email = authentication.getName();
        Genre genre = genreService.createGenre(email, genreRequest);
        return ResponseEntity.ok(ApiResponse.success("Thêm mới thành công một thể loại", genre));
    }

    @PutMapping("/admin/genres/{id}")
    public ResponseEntity<ApiResponse<Genre>> UpdateGenre(Authentication authentication,@PathVariable int id, @Valid @RequestBody GenreRequest genreRequest){
        String email = authentication.getName();
        Genre genre = genreService.updateGenre(email, id, genreRequest);
        return ResponseEntity.ok(ApiResponse.success("Sửa thành công một thể loại", genre));
    }

    @DeleteMapping("/admin/genres/{id}")
    public ResponseEntity<ApiResponse<Genre>> deleteGenre(Authentication authentication,
                                                          @PathVariable int id){
        String email = authentication.getName();
        genreService.deleteGenre(email, id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công một thể loại"));
    }

    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<List<Genre>>> getGenres(){
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success("Lấy genre thành công", genres));
    }

    @GetMapping("/genres/{slug}/mangas")
    public ResponseEntity<Page<MangaResponse>> getMangasByGenreSlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<MangaResponse> response = mangaService.getApprovedMangasByGenre(slug, page, size);

        return ResponseEntity.ok(response);
    }



}
