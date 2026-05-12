package com.clevora.clevora.service.user;

import com.clevora.clevora.dto.user.UserProfileRequest;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserResponse updateProfile(String email, UserProfileRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        if(request.getDisplayName() != null && !request.getDisplayName().isEmpty())
            user.setDisplayName(request.getDisplayName());
        if(request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty())
            user.setAvatarUrl(request.getAvatarUrl());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
