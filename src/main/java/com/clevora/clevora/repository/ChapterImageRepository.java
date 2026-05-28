package com.clevora.clevora.repository;

import com.clevora.clevora.entity.ChapterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterImageRepository extends JpaRepository<ChapterImage, Long> {
    List<ChapterImage> findByChapterIdOrderByPageNumberAsc(Integer chapterId);
}
