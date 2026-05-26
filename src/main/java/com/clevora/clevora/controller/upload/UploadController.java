package com.clevora.clevora.controller.upload;

import com.clevora.clevora.dto.common.ApiResponse;
import com.clevora.clevora.dto.upload.UploadResponse;
import com.clevora.clevora.service.upload.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;
    
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        Map<String, Object> result = cloudinaryService.uploadImage(file, folder);

        UploadResponse response = UploadResponse.builder()
                .imageUrl((String) result.get("secure_url"))
                .publicId((String) result.get("public_id"))
                .width((Integer) result.get("width"))
                .height((Integer) result.get("height"))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", response));
    }
}
