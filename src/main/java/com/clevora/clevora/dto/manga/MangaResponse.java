package com.clevora.clevora.dto.manga;

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

    // Use String instead of LocalDateTime to avoid Jackson 3 serialization issues
    // LocalDateTime -> toString() produces ISO-8601 format "2024-01-15T10:30:00"
    private String createdAt;

    private String updatedAt;

    private Set<String> genres;

    private Integer totalChapters;

    private Integer submittedById;

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
                .genres(
                        manga.getGenres()
                                .stream()
                                .map(genre -> genre.getName())
                                .collect(Collectors.toSet())
                )
                // total chapters
                .totalChapters(
                        manga.getChapters() != null
                                ? manga.getChapters().size()
                                : 0
                )
                .submittedById(
                        manga.getSubmittedBy() != null
                                ? manga.getSubmittedBy().getId()
                                : null
                )
                .build();
    }
}
