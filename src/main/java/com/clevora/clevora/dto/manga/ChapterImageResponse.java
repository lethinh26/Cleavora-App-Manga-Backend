package com.clevora.clevora.dto.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterImageResponse {
    private Long id;
    private String imageUrl;
    private Integer pageNumber;
}
