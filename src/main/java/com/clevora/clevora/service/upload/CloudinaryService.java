package com.clevora.clevora.service.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.clevora.clevora.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    public Map<String, Object> uploadImage(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!ALLOWED_TYPES.contains(contentType) && !contentType.startsWith("image/") && !contentType.equals("application/octet-stream"))) {
            throw new BadRequestException(
                "Chỉ hỗ trợ định dạng: JPEG, PNG, WebP, GIF. Nhận được: " + contentType);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("Kích thước file tối đa là 10MB");
        }

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", "clevora/" + (folder != null ? folder : "general"),
                    "resource_type", "image"
            );

            return cloudinary.uploader().upload(file.getBytes(), params);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }


    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
           
        }
    }
}
