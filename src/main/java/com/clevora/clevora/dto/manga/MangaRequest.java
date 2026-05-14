package com.clevora.clevora.dto.manga;

import com.clevora.clevora.entity.Manga;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MangaRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 255, message = "Slug must be less than 255 characters")
    private String slug;

    @Size(max = 5000, message = "Description is too long")
    private String description;

    @Size(max = 500, message = "Cover image URL too long")
    private String coverImageUrl;

    @Size(max = 200, message = "Author name too long")
    private String authorName;

    @Size(max = 200, message = "Artist name too long")
    private String artistName;

    private Manga.MangaStatus status;

    private Manga.ApprovalStatus approvalStatus;

    @NotEmpty(message = "At least one genre is required")
    private Set<Integer> genreIds;
}