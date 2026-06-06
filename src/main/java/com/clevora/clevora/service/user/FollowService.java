package com.clevora.clevora.service.user;

import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.dto.user.FollowListResponse;
import com.clevora.clevora.dto.user.FollowResponse;
import com.clevora.clevora.dto.user.FollowStatusResponse;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.entity.UserFollow;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserFollowRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;
    private final UserFollowRepository userFollowRepository;

    @Transactional
    public FollowResponse toggleFollow(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Truyện không tồn tại"));

        boolean exists = userFollowRepository.existsByUserIdAndMangaId(user.getId(), mangaId);

        if (exists) {
            userFollowRepository.deleteByUserIdAndMangaId(user.getId(), mangaId);
            int newCount = Math.max(0, manga.getFollowCount() - 1);
            manga.setFollowCount(newCount);
            mangaRepository.save(manga);
            return FollowResponse.builder()
                    .followed(false)
                    .followCount(newCount)
                    .build();
        } else {
            UserFollow follow = UserFollow.builder()
                    .user(user)
                    .manga(manga)
                    .build();
            userFollowRepository.save(follow);
            int newCount = manga.getFollowCount() + 1;
            manga.setFollowCount(newCount);
            mangaRepository.save(manga);
            return FollowResponse.builder()
                    .followed(true)
                    .followCount(newCount)
                    .build();
        }
    }

    public FollowStatusResponse getFollowStatus(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Truyện không tồn tại");
        }

        boolean followed = userFollowRepository.existsByUserIdAndMangaId(user.getId(), mangaId);
        return FollowStatusResponse.builder().followed(followed).build();
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollows(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Page<Manga> page = userFollowRepository.findApprovedMangasByUserId(user.getId(), pageable);

        return FollowListResponse.builder()
                .content(page.getContent().stream()
                        .map(MangaResponse::fromEntityManga)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
