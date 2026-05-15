package com.clevora.clevora.dto.manga;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreDTO {
    private Integer id;
    private String name;
    private String slug;

}