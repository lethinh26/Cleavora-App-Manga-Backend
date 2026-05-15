package com.clevora.clevora.dto.manga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SearchMangaRequest {
    public String title;
    public String author;
}
