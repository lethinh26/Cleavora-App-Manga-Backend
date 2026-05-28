package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByMangaIdOrderByChapterNumberDesc(Integer mangaId);
    List<Chapter> findByMangaIdOrderByChapterNumberAsc(Integer mangaId);
}
