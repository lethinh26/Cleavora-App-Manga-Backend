package com.clevora.clevora.dto.manga;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreRequest {
    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(min = 2,message = "Tên thể loại có nhiều hơn 2 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;
}
