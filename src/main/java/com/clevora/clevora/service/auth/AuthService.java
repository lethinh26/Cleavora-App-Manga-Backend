package com.clevora.clevora.service.auth;

import com.clevora.clevora.dto.auth.LoginRequest;
import com.clevora.clevora.dto.auth.LoginResponse;
import com.clevora.clevora.dto.auth.RegisterRequest;
import com.clevora.clevora.dto.user.ChangePassRequest;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.BadRequestException;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.UserRepository;
import com.clevora.clevora.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại trong hệ thống");
        }

        User user = User.builder()
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // Kiểm tra user có bị khoá không trước khi xác thực
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        if (!user.getActive()) {
            throw new BadRequestException("Tài khoản của bạn đã bị khoá. Vui lòng liên hệ quản trị viên.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
