package com.clevora.clevora.service.user;

import com.clevora.clevora.dto.manga.MangaResponse;
import com.clevora.clevora.dto.user.FavoriteListResponse;
import com.clevora.clevora.dto.user.LikeResponse;
import com.clevora.clevora.dto.user.LikeStatusResponse;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.entity.UserFavorite;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserFavoriteRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    @Transactional
    public LikeResponse toggleLike(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Truyện không tồn tại"));

        boolean exists = userFavoriteRepository.existsByUserIdAndMangaId(user.getId(), mangaId);

        if (exists) {
            userFavoriteRepository.deleteByUserIdAndMangaId(user.getId(), mangaId);
            int newCount = Math.max(0, manga.getLikeCount() - 1);
            manga.setLikeCount(newCount);
            mangaRepository.save(manga);
            return LikeResponse.builder()
                    .liked(false)
                    .likeCount(newCount)
                    .build();
        } else {
            UserFavorite favorite = UserFavorite.builder()
                    .user(user)
                    .manga(manga)
                    .build();
            userFavoriteRepository.save(favorite);
            int newCount = manga.getLikeCount() + 1;
            manga.setLikeCount(newCount);
            mangaRepository.save(manga);
            return LikeResponse.builder()
                    .liked(true)
                    .likeCount(newCount)
                    .build();
        }
    }

    public LikeStatusResponse getLikeStatus(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Truyện không tồn tại");
        }

        boolean liked = userFavoriteRepository.existsByUserIdAndMangaId(user.getId(), mangaId);
        return LikeStatusResponse.builder().liked(liked).build();
    }

    public FavoriteListResponse getFavorites(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Page<Manga> page = userFavoriteRepository.findApprovedMangasByUserId(user.getId(), pageable);

        return FavoriteListResponse.builder()
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
