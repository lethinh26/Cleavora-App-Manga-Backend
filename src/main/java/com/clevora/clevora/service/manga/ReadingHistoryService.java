package com.clevora.clevora.service.manga;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clevora.clevora.dto.user.ReadingHistoryResponse;
import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.ReadingHistory;
import com.clevora.clevora.entity.User;
import com.clevora.clevora.exception.ResourceNotFoundException;
import com.clevora.clevora.repository.ChapterRepository;
import com.clevora.clevora.repository.MangaRepository;
import com.clevora.clevora.repository.ReadingHistoryRepository;
import com.clevora.clevora.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReadingHistoryService {
    private final ChapterRepository chapterRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final MangaRepository mangaRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReadingHistoryResponse> getReadingHistories(String email) {
        // check var
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return readingHistoryRepository.findByUserEmailOrderByLastReadAtDesc(email).stream()
                .map(history -> {
                    Double chapterNumber = null;
                    if (history.getChapterId() != null) {
                        chapterNumber = chapterRepository.findById(history.getChapterId())
                                .map(ch -> ch.getChapterNumber())
                                .orElse(null);
                    }
                    return ReadingHistoryResponse.buildingFromEntity(history, chapterNumber);
                })
                .toList();
    }

    @Transactional
    public ReadingHistoryResponse upsertReadingHistory(String email, Integer mangaId, Integer chapterId, Integer lastPage) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found"));

        // B9: Don't validate chapter existence — chapter may have been deleted.

        Optional<ReadingHistory> historyOpt = readingHistoryRepository.findByUserAndManga(user, manga);

        ReadingHistory readingHistory;
        if (historyOpt.isPresent()) {
            readingHistory = historyOpt.get();
            readingHistory.setChapterId(chapterId);
            readingHistory.setLastPage(lastPage);
            readingHistory.setLastReadAt(LocalDateTime.now());
        } else {
            // Tạo mới nếu chưa có
            readingHistory = new ReadingHistory();
            readingHistory.setUser(user);
            readingHistory.setManga(manga);
            readingHistory.setChapterId(chapterId);
            readingHistory.setLastPage(lastPage);
            readingHistory.setLastReadAt(LocalDateTime.now());
        }

        readingHistoryRepository.save(readingHistory);
        return ReadingHistoryResponse.buildingFromEntity(readingHistory);
    }

    @Transactional
    public void deleteReadingHistoryByManga(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found"));

        ReadingHistory history = readingHistoryRepository.findByUserAndManga(user, manga)
                .orElseThrow(() -> new ResourceNotFoundException("Reading history not found for this manga"));

        readingHistoryRepository.delete(history);
    }

    @Transactional
    public void deleteAllReadingHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        readingHistoryRepository.deleteByUser(user);
    }

    @Transactional(readOnly = true)
    public ReadingHistory getReadingHistoryByManga(String email, Integer mangaId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found"));

        return readingHistoryRepository.findByUserAndManga(user, manga).orElse(null);
    }

    public Double getChapterNumber(Integer chapterId) {
        if (chapterId == null) return null;
        return chapterRepository.findById(chapterId)
                .map(ch -> ch.getChapterNumber())
                .orElse(null);
    }

}
