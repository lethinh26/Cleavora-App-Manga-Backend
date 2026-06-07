package com.clevora.clevora.service.user;

import com.clevora.clevora.dto.user.DashboardStatsResponse;
import com.clevora.clevora.dto.user.UserResponse;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.BadRequestException;
import com.clevora.clevora.exception.ForbiddenException;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.repository.ChapterRepository;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;
    private final ChapterRepository chapterRepository;

    // ============ USER MANAGEMENT ============

    /**
     * #43 - Danh sách người dùng (phân trang)
     */
    public Page<UserResponse> getAllUsers(int page, int size) {
        Page<User> users = userRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build());
    }

    /**
     * #44 - Vô hiệu hóa / kích hoạt tài khoản
     */
    @Transactional
    public UserResponse toggleUserActive(String adminEmail, Integer userId) {
        User admin = findUserByEmail(adminEmail);
        validateAdminRole(admin);

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Không cho tự vô hiệu hóa chính mình
        if (admin.getId().equals(targetUser.getId())) {
            throw new BadRequestException("Không thể vô hiệu hóa chính mình");
        }

        // Không cho phép vô hiệu hóa tài khoản SUPERADMIN
        if (targetUser.getRole() == User.Role.SUPERADMIN) {
            throw new ForbiddenException("Không thể vô hiệu hóa tài khoản SUPERADMIN");
        }

        targetUser.setActive(!targetUser.getActive());
        userRepository.save(targetUser);

        return UserResponse.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .displayName(targetUser.getDisplayName())
                .avatarUrl(targetUser.getAvatarUrl())
                .role(targetUser.getRole().name())
                .active(targetUser.getActive())
                .createdAt(targetUser.getCreatedAt())
                .build();
    }

    /**
     * #46 - SUPERADMIN thay đổi role user
     */
    @Transactional
    public UserResponse changeUserRole(String superadminEmail, Integer userId, String newRoleStr) {
        User superadmin = findUserByEmail(superadminEmail);

        if (superadmin.getRole() != User.Role.SUPERADMIN) {
            throw new ForbiddenException("Chỉ SUPERADMIN mới có quyền đổi role");
        }

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (superadmin.getId().equals(targetUser.getId())) {
            throw new BadRequestException("Không thể thay đổi role của chính mình");
        }

        User.Role newRole;
        try {
            newRole = User.Role.valueOf(newRoleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role không hợp lệ: " + newRoleStr);
        }

        targetUser.setRole(newRole);
        userRepository.save(targetUser);

        return UserResponse.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .displayName(targetUser.getDisplayName())
                .avatarUrl(targetUser.getAvatarUrl())
                .role(targetUser.getRole().name())
                .active(targetUser.getActive())
                .createdAt(targetUser.getCreatedAt())
                .build();
    }

    // ============ DASHBOARD ============

    /**
     * #45 - Thống kê tổng quan
     */
    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalMangas(mangaRepository.count())
                .totalChapters(chapterRepository.count())
                .totalPendingMangas(mangaRepository.countByApprovalStatus(Manga.ApprovalStatus.PENDING))
                .build();
    }

    // ============ MANGA APPROVAL ============

    /**
     * #34 - Danh sách truyện PENDING
     */
    public Page<MangaResponse> getPendingMangas(int page, int size) {
        Page<Manga> mangas = mangaRepository.findByApprovalStatus(
                Manga.ApprovalStatus.PENDING,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return mangas.map(MangaResponse::fromEntityManga);
    }

    /**
     * #35 - Duyệt truyện
     */
    @Transactional
    public MangaResponse approveManga(String adminEmail, Integer mangaId) {
        User admin = findUserByEmail(adminEmail);
        validateAdminRole(admin);

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện"));

        if (manga.getApprovalStatus() != Manga.ApprovalStatus.PENDING) {
            throw new BadRequestException("Truyện không ở trạng thái chờ duyệt");
        }

        manga.setApprovalStatus(Manga.ApprovalStatus.APPROVED);
        manga.setApprovedBy(admin);
        manga.setApprovedAt(LocalDateTime.now());
        manga.setRejectReason(null);

        return MangaResponse.fromEntityManga(mangaRepository.save(manga));
    }

    /**
     * #36 - Từ chối truyện
     */
    @Transactional
    public MangaResponse rejectManga(String adminEmail, Integer mangaId, String rejectReason) {
        User admin = findUserByEmail(adminEmail);
        validateAdminRole(admin);

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện"));

        if (manga.getApprovalStatus() != Manga.ApprovalStatus.PENDING) {
            throw new BadRequestException("Truyện không ở trạng thái chờ duyệt");
        }

        manga.setApprovalStatus(Manga.ApprovalStatus.REJECTED);
        manga.setRejectReason(rejectReason);
        manga.setApprovedBy(admin);
        manga.setApprovedAt(LocalDateTime.now());

        return MangaResponse.fromEntityManga(mangaRepository.save(manga));
    }

    // ============ HELPERS ============

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private void validateAdminRole(User user) {
        if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.SUPERADMIN) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
    }
}
