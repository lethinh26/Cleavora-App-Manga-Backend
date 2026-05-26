package com.clevora.clevora.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {
    private String imageUrl;    // secure_url từ Cloudinary
    private String publicId;    // public_id để xóa ảnh sau này
    private Integer width;
    private Integer height;
}
