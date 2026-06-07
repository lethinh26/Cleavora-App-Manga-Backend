package com.clevora.clevora.service.manga;

import com.clevora.clevora.dto.manga.ChapterDetailResponse;
import com.clevora.clevora.dto.manga.ChapterImageResponse;
import com.clevora.clevora.dto.manga.ChapterRequest;
import com.clevora.clevora.dto.manga.ChapterResponse;
import com.clevora.clevora.entity.Chapter;
import com.clevora.clevora.entity.ChapterImage;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ForbiddenException;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.ChapterImageRepository;
import com.clevora.clevora.repository.ChapterRepository;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterImageRepository chapterImageRepository;
    private final MangaRepository mangaRepository;
    private final UserRepository userRepository;

    public List<ChapterResponse> getChaptersByMangaId(Integer mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Truyện không tồn tại!");
        }
        return chapterRepository.findByMangaIdOrderByChapterNumberDesc(mangaId)
                .stream()
                .map(this::mapToChapterResponse)
                .collect(Collectors.toList());
    }

    public ChapterDetailResponse getChapterDetail(Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter không tồn tại!"));

        List<ChapterImageResponse> images = chapterImageRepository.findByChapterIdOrderByPageNumberAsc(chapterId)
                .stream()
                .map(img -> ChapterImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .pageNumber(img.getPageNumber())
                        .build())
                .collect(Collectors.toList());

        return ChapterDetailResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .viewCount(chapter.getViewCount() != null ? chapter.getViewCount() : 0)
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt() != null ? chapter.getUpdatedAt() : chapter.getCreatedAt())
                .images(images)
                .build();
    }

    @Transactional
    public ChapterResponse createChapter(String email, Integer mangaId, ChapterRequest request) {
        Manga manga = validateAndGetMangaForEdit(email, mangaId);

        Chapter chapter = Chapter.builder()
                .manga(manga)
                .title(request.getTitle())
                .chapterNumber(request.getChapterNumber())
                .pageCount(request.getImageUrls() != null ? request.getImageUrls().size() : 0)
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int pageNumber = 1;
            for (String url : request.getImageUrls()) {
                ChapterImage image = ChapterImage.builder()
                        .chapter(savedChapter)
                        .imageUrl(url)
                        .pageNumber(pageNumber++)
                        .build();
                chapterImageRepository.save(image);
            }
        }

        return mapToChapterResponse(savedChapter);
    }

    @Transactional
    public ChapterResponse updateChapter(String email, Integer chapterId, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter không tồn tại!"));

        // Load manga eagerly to avoid LazyInitializationException
        Integer mangaId = chapter.getManga().getId();
        validateAndGetMangaForEdit(email, mangaId);

        if (request.getTitle() != null || request.getChapterNumber() != null) {
            if (request.getTitle() != null) chapter.setTitle(request.getTitle());
            if (request.getChapterNumber() != null) chapter.setChapterNumber(request.getChapterNumber());
        }

        if (request.getImageUrls() != null) {
            // Xoá ảnh cũ và thêm ảnh mới
            List<ChapterImage> oldImages = chapterImageRepository.findByChapterIdOrderByPageNumberAsc(chapterId);
            chapterImageRepository.deleteAll(oldImages);
            chapterImageRepository.flush();

            int pageNumber = 1;
            for (String url : request.getImageUrls()) {
                ChapterImage image = ChapterImage.builder()
                        .chapter(chapter)
                        .imageUrl(url)
                        .pageNumber(pageNumber++)
                        .build();
                chapterImageRepository.save(image);
            }
            chapter.setPageCount(request.getImageUrls().size());
        }

        Chapter updatedChapter = chapterRepository.save(chapter);
        return mapToChapterResponse(updatedChapter);
    }

    @Transactional
    public void deleteChapter(String email, Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter không tồn tại!"));

        validateAndGetMangaForEdit(email, chapter.getManga().getId());

        chapterRepository.delete(chapter);
    }

    @Transactional
    public void incrementViewCount(Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter không tồn tại!"));

        Manga manga = chapter.getManga();
        manga.setViewCount(manga.getViewCount() + 1);
        mangaRepository.save(manga);
    }

    private Manga validateAndGetMangaForEdit(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Truyện không tồn tại!"));

        boolean isAdmin = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.SUPERADMIN;
        boolean isOwner = manga.getSubmittedBy() != null && manga.getSubmittedBy().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("Bạn không có quyền thao tác trên truyện này!");
        }

        return manga;
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .viewCount(chapter.getViewCount() != null ? chapter.getViewCount() : 0)
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt() != null ? chapter.getUpdatedAt() : chapter.getCreatedAt())
                .build();
    }
}
