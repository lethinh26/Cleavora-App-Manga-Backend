package com.clevora.clevora.dto.manga;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.clevora.clevora.entity.Manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MangaResponse {

    private Integer id;

    private String title;

    private String slug;

    private String description;

    private String coverImageUrl;

    private String authorName;

    private String artistName;

    private Manga.MangaStatus status;

    private Manga.ApprovalStatus approvalStatus;

    private Integer viewCount;

    private Integer likeCount;

    private Integer followCount;

    private String createdAt;

    private String updatedAt;

    private Set<String> genres;

    private Integer totalChapters;

    private String submittedByEmail;

    private String submittedByName;

    // =====================================
    // BUILDER FROM ENTITY
    // =====================================

    public static MangaResponse fromEntityManga(Manga manga) {
        return MangaResponse.builder()
                .id(manga.getId())
                .title(manga.getTitle())
                .slug(manga.getSlug())
                .description(manga.getDescription())
                .coverImageUrl(manga.getCoverImageUrl())
                .authorName(manga.getAuthorName())
                .artistName(manga.getArtistName())
                .status(manga.getStatus())
                .approvalStatus(manga.getApprovalStatus())
                .viewCount(manga.getViewCount())
                .likeCount(manga.getLikeCount())
                .followCount(manga.getFollowCount())
                // Convert LocalDateTime to ISO-8601 String directly
                .createdAt(manga.getCreatedAt() != null
                        ? manga.getCreatedAt().toString() : null)
                .updatedAt(manga.getUpdatedAt() != null
                        ? manga.getUpdatedAt().toString() : null)
                .genres(safeGetGenres(manga))
                .totalChapters(safeGetChapterCount(manga))
                .submittedByEmail(safeGetSubmittedByEmail(manga))
                .submittedByName(safeGetSubmittedByName(manga))
                .build();
    }

    // =====================================
    // SAFE LAZY-LOADING HELPERS
    // =====================================

    private static Set<String> safeGetGenres(Manga manga) {
        try {
            if (manga.getGenres() != null) {
                return manga.getGenres().stream()
                        .map(genre -> genre.getName())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            // LazyInitializationException — session closed
        }
        return Collections.emptySet();
    }

    private static Integer safeGetChapterCount(Manga manga) {
        try {
            if (manga.getChapters() != null) {
                return manga.getChapters().size();
            }
        } catch (Exception e) {
            // LazyInitializationException — session closed
        }
        return 0;
    }

    private static String safeGetSubmittedByEmail(Manga manga) {
        try {
            if (manga.getSubmittedBy() != null) {
                return manga.getSubmittedBy().getEmail();
            }
        } catch (Exception e) {
            // LazyInitializationException or EntityNotFoundException
        }
        return null;
    }

    private static String safeGetSubmittedByName(Manga manga) {
        try {
            if (manga.getSubmittedBy() != null) {
                return manga.getSubmittedBy().getDisplayName();
            }
        } catch (Exception e) {
            // LazyInitializationException or EntityNotFoundException
        }
        return null;
    }
}

