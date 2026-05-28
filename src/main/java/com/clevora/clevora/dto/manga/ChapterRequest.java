package com.clevora.clevora.dto.manga;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {
    
    private String title;

    @NotNull(message = "Chapter number is required")
    private Double chapterNumber;

    // List of Cloudinary Image URLs uploaded from Frontend
    private List<String> imageUrls;
}
