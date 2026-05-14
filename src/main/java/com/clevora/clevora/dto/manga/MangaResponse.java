package com.clevora.clevora.dto.manga;

import com.clevora.clevora.entity.Manga;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<String> genres;

    private Integer totalChapters;

    // =====================================
    // BUILDER FROM ENTITY
    // =====================================

    public static MangaResponse fromEntity(Manga manga) {

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
                .createdAt(manga.getCreatedAt())
                .updatedAt(manga.getUpdatedAt())

                // genre names
                .genres(
                        manga.getGenres()
                                .stream()
                                .map(genre -> genre.getName())
                                .collect(Collectors.toSet())
                )

                // total chapters
//                .totalChapters(
//                        manga.getChapters() != null
//                                ? manga.getChapters().size()
//                                : 0
//                )

                .build();
    }
}