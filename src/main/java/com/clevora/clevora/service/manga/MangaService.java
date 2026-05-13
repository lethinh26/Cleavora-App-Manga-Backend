package com.clevora.clevora.service.manga;

import com.clevora.clevora.dto.manga.MangaRequest;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.entity.Genre;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.GenreRepository;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class MangaService {
    // cud
    private MangaRepository mangaRepository;
    private UserRepository userRepository;
    private final GenreRepository genreRepository;

    public MangaResponse createManga(String email, MangaRequest mangaRequest) {

        validateAdminRole(email);

        Set<Genre> genres = getGenresFromIds(mangaRequest.getGenreIds());

        Manga manga = Manga.builder()
                .title(mangaRequest.getTitle())
                .slug(mangaRequest.getSlug())
                .description(mangaRequest.getDescription())
                .coverImageUrl(mangaRequest.getCoverImageUrl())
                .authorName(mangaRequest.getAuthorName())
                .artistName(mangaRequest.getArtistName())
                .status(Manga.MangaStatus.ONGOING)
                .approvalStatus(Manga.ApprovalStatus.APPROVED)
                .genres(genres)
                .viewCount(0)
                .likeCount(0)
                .followCount(0)
                .build();

        Manga savedManga = mangaRepository.save(manga);

        return MangaResponse.fromEntity(savedManga);
    }

    public MangaResponse updateManga(
            String email,
            Integer mangaId,
            MangaRequest mangaRequest
    ) {

        validateAdminRole(email);

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manga không tồn tại"));

        Set<Genre> genres = getGenresFromIds(mangaRequest.getGenreIds());

        manga.setTitle(mangaRequest.getTitle());
        manga.setSlug(mangaRequest.getSlug());
        manga.setDescription(mangaRequest.getDescription());
        manga.setCoverImageUrl(mangaRequest.getCoverImageUrl());
        manga.setAuthorName(mangaRequest.getAuthorName());
        manga.setArtistName(mangaRequest.getArtistName());
        manga.setStatus(mangaRequest.getStatus());
        manga.setApprovalStatus(mangaRequest.getApprovalStatus());
        manga.setGenres(genres);

        Manga updatedManga = mangaRepository.save(manga);

        return MangaResponse.fromEntity(updatedManga);
    }


    public boolean deleteManga(String email, Integer mangaId) {
        validateAdminRole(email);
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manga không tồn tại"));
        mangaRepository.delete(manga);
        return true;
    }

    private void validateAdminRole(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.SUPERADMIN) {
            throw new RuntimeException("Bạn không có quyền thực hiện hành động này");
        }
    }

    private Set<Genre> getGenresFromIds(Set<Integer> genreIds) {
        Set<Genre> genres = new HashSet<>();
        for (Integer genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Genre không tồn tại với id: " + genreId
                            ));
            genres.add(genre);
        }
        return genres;
    }
}
