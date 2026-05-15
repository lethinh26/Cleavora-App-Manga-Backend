package com.clevora.clevora.service.manga;

import com.clevora.clevora.dto.manga.MangaRequest;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.entity.Genre;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ForbiddenException;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.GenreRepository;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.*;

@Service
@AllArgsConstructor
public class MangaService {
    // cud
    private MangaRepository mangaRepository;
    private UserRepository userRepository;
    private final GenreRepository genreRepository;

    @Transactional
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

        return MangaResponse.fromEntityManga(savedManga);
    }

    @Transactional
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

        return MangaResponse.fromEntityManga(updatedManga);
    }

    @Transactional
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
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
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

    public List<MangaResponse> getApprovedMangas(int page, int size, String sortBy, String statusStr) {
        Sort sort = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "likes" -> Sort.by(Sort.Direction.DESC, "likeCount");
            case "follows" -> Sort.by(Sort.Direction.DESC, "followCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Manga.MangaStatus statusEnum = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                statusEnum = Manga.MangaStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Trạng thái truyện không hợp lệ: " + statusStr);
            }
        }
        return mangaRepository.findApprovedMangas(Manga.ApprovalStatus.APPROVED, statusEnum, PageRequest.of(page, size, sort))
                .stream().map(MangaResponse::fromEntityManga).toList();
    }

    public MangaResponse getMangaDetailBySlug(String slug) {
        Manga manga = mangaRepository.findApprovedMangaBySlugWithGenres(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện hoặc truyện chưa được duyệt!"));
        return MangaResponse.fromEntityManga(manga);
    }

    public Page<MangaResponse> searchMangas(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Manga> mangaPage = mangaRepository.searchApprovedMangas(keyword.trim(), pageable);
        return mangaPage.map(MangaResponse::fromEntityManga);
    }

    public Page<MangaResponse> getApprovedMangasByGenre(String genreSlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Manga> mangaPage = mangaRepository.findApprovedMangasByGenreSlug(genreSlug, pageable);
        return mangaPage.map(MangaResponse::fromEntityManga);
    }

    @Transactional
    public MangaResponse submitManga(MangaRequest request, String userEmail) {
        // check var người dùng
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));

        List<Genre> genresList = genreRepository.findAllById(request.getGenreIds());
        Set<Genre> genres = new HashSet<>(genresList);

        if (genres.isEmpty() || genres.size() != request.getGenreIds().size()) {
            throw new RuntimeException("Một hoặc nhiều thể loại không tồn tại!");
        }

        Manga newManga = new Manga();
        newManga.setTitle(request.getTitle());
        newManga.setSlug(request.getSlug());
        newManga.setDescription(request.getDescription());
        newManga.setCoverImageUrl(request.getCoverImageUrl());
        newManga.setAuthorName(request.getAuthorName());
        newManga.setArtistName(request.getArtistName());
        newManga.setGenres(genres);
        newManga.setStatus(request.getStatus() != null ? request.getStatus() : Manga.MangaStatus.ONGOING);
        newManga.setApprovalStatus(Manga.ApprovalStatus.PENDING);
        newManga.setSubmittedBy(currentUser);
        newManga.setViewCount(0);
        newManga.setLikeCount(0);
        newManga.setFollowCount(0);

        Manga savedManga = mangaRepository.save(newManga);
        return MangaResponse.fromEntityManga(savedManga);
    }

    public Page<MangaResponse> getMyMangas(String userEmail, String approvalStatusStr, int page, int size) {
        Manga.ApprovalStatus statusEnum = null;
        if (approvalStatusStr != null && !approvalStatusStr.trim().isEmpty()) {
            try {
                statusEnum = Manga.ApprovalStatus.valueOf(approvalStatusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Trạng thái phê duyệt không hợp lệ");
            }
        }
        Page<Manga> mangaPage = mangaRepository.findMyMangas(userEmail, statusEnum, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return mangaPage.map(MangaResponse::fromEntityManga);
    }

    @Transactional
    public MangaResponse updateMyManga(int mangaId, MangaRequest request, String userEmail) {
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện!"));

        if (!manga.getSubmittedBy().getEmail().equals(userEmail)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa truyện của người khác!");
        }
        if (manga.getApprovalStatus() == Manga.ApprovalStatus.APPROVED) {
            throw new ResourceNotFoundException("Truyện đã được duyệt, không thể chỉnh sửa!");
        }

        List<Genre> genresList = genreRepository.findAllById(request.getGenreIds());
        if (genresList.isEmpty() || genresList.size() != request.getGenreIds().size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều thể loại không tồn tại!");
        }
        manga.setGenres(new HashSet<>(genresList));

        manga.setTitle(request.getTitle());
        manga.setSlug(request.getSlug());
        manga.setDescription(request.getDescription());
        manga.setCoverImageUrl(request.getCoverImageUrl());
        manga.setAuthorName(request.getAuthorName());
        manga.setArtistName(request.getArtistName());

        if (request.getStatus() != null) {
            manga.setStatus(request.getStatus());
        }

        manga.setApprovalStatus(Manga.ApprovalStatus.PENDING);
        manga.setRejectReason(null);
        Manga updatedManga = mangaRepository.save(manga);
        return MangaResponse.fromEntityManga(updatedManga);
    }

}
