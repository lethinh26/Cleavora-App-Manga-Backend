package com.clevora.clevora.repository;

import com.clevora.clevora.entity.Manga;
import com.clevora.clevora.entity.ReadingHistory;
import com.clevora.clevora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {

    List<ReadingHistory> findByUserEmail(String userEmail);

    List<ReadingHistory> findByUserEmailOrderByLastReadAtDesc(String userEmail);

    Optional<ReadingHistory> findByUserAndManga(User user, Manga manga);

    void deleteByUser(User user);

    List<ReadingHistory> findByUser(User user);
}
